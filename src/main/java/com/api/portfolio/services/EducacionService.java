package com.api.portfolio.services;

import com.api.portfolio.entities.Educacion;
import com.api.portfolio.entities.Response;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.repository.EducacionRepository;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobStorageException;
import io.micrometer.common.util.StringUtils;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class EducacionService implements IEducacionService {

    private final EducacionRepository repo;
    private final IUsuarioService usuarioService;

    String connectionString = "DefaultEndpointsProtocol=https;AccountName=filestorage1110;AccountKey=EuqxmXMC1mLwPni08YBu+kQP7mEyZnOAY4W+vmR4+ntD8lAhYe9CEiimgVMpCAsICl/HH8Ni/0s6+AStCkKhbg==;EndpointSuffix=core.windows.net";

String containerName = "container";
    String defaultImageName = "default.png";

    @Override
    public ResponseEntity<Educacion> findById(long id) {
        Optional<Educacion> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(found.get());
    }

    @Override
    public ResponseEntity<Educacion> deleteEducation(long id) {
        Optional<Educacion> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        repo.delete(found.get());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Educacion>> getEducations(long idUsuario) {
        Usuario usuario = usuarioService.getUser(idUsuario).getBody();
        if (usuario == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.ok().body(usuario.getEducaciones());
    }

    @Override
    public ResponseEntity<Educacion> postEducation(long idUsuario, Educacion edu) {
        Usuario usuario = usuarioService.getUser(idUsuario).getBody();
        if (usuario == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        edu.setUsuario(usuario);
        Educacion saved = repo.save(edu);
        URI localizacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(localizacion).body(saved);
    }

    @Override
    public ResponseEntity<Educacion> putEducation(long idUsuario, long idExp, Educacion edu) {
        Usuario usuario = usuarioService.getUser(idUsuario).getBody();
        if (usuario == null) {
            return ResponseEntity.unprocessableEntity().build();
        }
        Optional<Educacion> found = repo.findById(idExp);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        edu.setUsuario(usuario);
        edu.setId(found.get().getId());
        edu.setLogo(found.get().getLogo());
        repo.save(edu);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> getImageEdu(long idEdu) {
        Optional<Educacion> found = repo.findById(idEdu);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String pathImagen = found.get().getLogo();
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
    public ResponseEntity<?> putImageEdu(long id, MultipartFile imagen) throws CreatingDirectoryImageException {
        Optional<Educacion> expFound = repo.findById(id);
        if (!expFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Educacion educacion = expFound.get();
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        try {
            
            String nombreImagen = "edu_" + educacion.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());

            
            BlobClient blobClient = containerClient.getBlobClient(nombreImagen);

            
            blobClient.upload(imagen.getInputStream(), imagen.getSize());

            
            educacion.setLogo(blobClient.getBlobUrl());
            repo.save(educacion);
        } catch (IOException | BlobStorageException ex) {
            throw new CreatingDirectoryImageException("No se pudo crear el directorio de imágenes de la educaciones", (IOException) ex);
        }

        return ResponseEntity.ok().build();
    }

}
