package com.api.portfolio.services;

import com.api.portfolio.entities.Response;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.repository.UsuarioRepository;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.IOException;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobStorageException;
import io.micrometer.common.util.StringUtils;


@Service
public class UsuarioService implements IUsuarioService {

    String connectionString = "DefaultEndpointsProtocol=https;AccountName=filestorage1110;AccountKey=EuqxmXMC1mLwPni08YBu+kQP7mEyZnOAY4W+vmR4+ntD8lAhYe9CEiimgVMpCAsICl/HH8Ni/0s6+AStCkKhbg==;EndpointSuffix=core.windows.net";

    String containerName = "container";
    String defaultImageName = "default.png";

    @Autowired
    private UsuarioRepository repo;

    @Override
    public ResponseEntity<Usuario> getUser(long id) {
        Optional<Usuario> saved = repo.findById(id);
        if (!saved.isPresent()) {
                return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(saved.get());
    }

    @Override
    public ResponseEntity<Usuario> putUser(long id, Usuario usuario) {
        Optional<Usuario> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        usuario.setId(found.get().getId());
        repo.save(usuario);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<Usuario>> getUsers() {
        return ResponseEntity.ok().body(repo.findAll());
    }

    @Override
    public ResponseEntity<String> actualizarImagenUsuario(long id, MultipartFile imagen) throws CreatingDirectoryImageException {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Usuario user = userFound.get();
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);

        try {
            
            String nombreImagen = "user_" + user.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());

            
            BlobClient blobClient = containerClient.getBlobClient(nombreImagen);

            
            blobClient.upload(imagen.getInputStream(), imagen.getSize());

            
            user.setImagen(blobClient.getBlobUrl());
            repo.save(user);
        } catch (IOException | BlobStorageException ex) {
            throw new CreatingDirectoryImageException("No se pudo crear el directorio de imágenes del usuario", (IOException) ex);
        }

        return ResponseEntity.ok().build();
    }

    
@Override
public ResponseEntity<?> getImage(long id) {
    Optional<Usuario> userFound = repo.findById(id);
    if (!userFound.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    String pathImagen = userFound.get().getImagen();
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
    public ResponseEntity<?> putUserTitulo(long id, String titulo) {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        userFound.get().setTitulo(titulo);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo el titulo"));
    }

    @Override
    public ResponseEntity<?> putUserDescripcion(long id, String descripcion) {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        userFound.get().setDescripcion(descripcion);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo la descripcion"));
    }

    @Override
    public ResponseEntity<?> putUserName(long id, String nuevoNombre) {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userFound.get().setNombre(nuevoNombre);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo el nombre"));
    }

    @Override
    public ResponseEntity<?> putUserLastName(long id, String nuevoApellido) {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        userFound.get().setApellido(nuevoApellido);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo el apellido"));
    }

}
