package com.api.portfolio.services;

import com.api.portfolio.entities.Experiencia;
import com.api.portfolio.entities.Response;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.repository.ExperienciaRepository;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import io.micrometer.common.util.StringUtils;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class ExperienciaService implements IExperienciaService {
    
    private final ExperienciaRepository repo;
    private final IUsuarioService usuarioService;
    
    String connectionString = "DefaultEndpointsProtocol=https;AccountName=imagenesangel;AccountKey=cP2YI4Y07S2SdvZaXnqU0lOaEUISqDXDzKkOFaVrgPTLymnQEp46MPqL4JF1OJCtaSQBCmhO7CpG+AStSn7HZA==;EndpointSuffix=core.windows.net";
    String containerName = "files";
    String defaultImageName = "default.png";

    
    @Override
    public ResponseEntity<Experiencia> findById(long id) {
        Optional<Experiencia> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(found.get());
    }
    
    @Override
    public ResponseEntity<Experiencia> putExperience(long idUsuario, long idExperiencia, Experiencia exp) {
        Usuario userFound = usuarioService.getUser(idUsuario).getBody();
        if (userFound == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        Optional<Experiencia> found = repo.findById(idExperiencia);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        exp.setId(found.get().getId());
        exp.setLogoEmpresa(found.get().getLogoEmpresa());
        exp.setUsuario(userFound);
        repo.save(exp);
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<Experiencia> deleteExperience(long id) {
        Optional<Experiencia> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        repo.delete(found.get());
        return ResponseEntity.noContent().build();
    }
    
    @Override
    public ResponseEntity<List<Experiencia>> getExperiences(long idUsuario) {
        Usuario userFound = usuarioService.getUser(idUsuario).getBody();
        if (userFound == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().body(userFound.getExperiencias());
    }
    
    @Override
    public ResponseEntity<Experiencia> postExperience(long idUsuario, Experiencia experiencia) {
        Usuario userFound = usuarioService.getUser(idUsuario).getBody();
        if (userFound == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        experiencia.setUsuario(userFound);
        Experiencia saved = repo.save(experiencia);
        URI localizacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(localizacion).body(saved);
    }
    
    @Override
public ResponseEntity<?> getImageExpById(long id) {
    Optional<Experiencia> expFound = repo.findById(id);
    if (!expFound.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    String pathImagen = expFound.get().getLogoEmpresa();
    if (StringUtils.isEmpty(pathImagen)) {
        
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        
        BlobClient defaultImageBlobClient = containerClient.getBlobClient(defaultImageName);
        if (!defaultImageBlobClient.exists()) {
            
            return ResponseEntity.notFound().build();
        }

        String defaultImageUrl = defaultImageBlobClient.getBlobUrl();

        return ResponseEntity.ok().body(new Response(defaultImageUrl));
    } else {
        
        return ResponseEntity.ok().body(new Response(pathImagen));
    }
}
    
    @Override
public ResponseEntity<String> putImageExp(long id, MultipartFile imagen) throws CreatingDirectoryImageException {
    Optional<Experiencia> expFound = repo.findById(id);
    if (!expFound.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    Experiencia experiencia = expFound.get();
    BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
    BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

    try {
        // Generar un nombre único para la imagen
        String nombreImagen = "exp_" + experiencia.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());

        // Obtener el BlobClient para el nuevo blob
        BlobClient blobClient = containerClient.getBlobClient(nombreImagen);

        // Cargar la imagen al blob
        blobClient.upload(imagen.getInputStream(), imagen.getSize());

        // Actualizar la URL de la imagen de la experiencia en la base de datos
        experiencia.setLogoEmpresa(blobClient.getBlobUrl());
        repo.save(experiencia);
    } catch (IOException | BlobStorageException ex) {
        throw new CreatingDirectoryImageException("No se pudo crear el directorio de imágenes de la experiencia", (IOException) ex);
    }

    return ResponseEntity.ok().build();
}
    
}
