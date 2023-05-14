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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private UsuarioRepository repo;

    private final String root_folder = "src/main/resources/static/userImage/";

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
            return ResponseEntity
                    .notFound()
                    .build();
        }
        Usuario user = userFound.get();
        if (user.getImagen() == null) {
            try {
                // Generar un nombre único para la imagen
                String nombreImagen = "user_" + user.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());

                // Guardar la imagen en la carpeta del usuario
                Path userImageFolder = Paths.get(root_folder, "user_imagen", Long.toString(user.getId()));
                Files.createDirectories(userImageFolder);
                Path userImagePath = userImageFolder.resolve(nombreImagen);
                Files.write(userImagePath, imagen.getBytes());

                // Actualizar la imagen del usuario en la base de datos
                user.setImagen(userImagePath.toString());
                repo.save(user);
            } catch (IOException ex) {
                throw new CreatingDirectoryImageException("No se pudo crear el directorio de imágenes del usuario", ex);
            } 

        } else {
            try {
                // Actualizar la imagen existente del usuario
                Path userImagePath = Paths.get(user.getImagen());
                Files.write(userImagePath, imagen.getBytes());
            } catch (IOException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo almacenar la imagen en el directorio, vuelva a intentarlo mas tarde");
            }
        }

        return ResponseEntity.ok().build();
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
            if(!userFound.isPresent()){
                return ResponseEntity.notFound().build();
            }
        userFound.get().setNombre(nuevoNombre);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo el nombre"));
    }

    @Override
    public ResponseEntity<?> putUserLastName(long id, String nuevoApellido) {
        Optional<Usuario> userFound = repo.findById(id);
            if(!userFound.isPresent()){
                return ResponseEntity.notFound().build();
            }
        userFound.get().setApellido(nuevoApellido);
        repo.save(userFound.get());
        return ResponseEntity.ok().body(new Response("Se actualizo el apellido"));
    }

    @Override
    public ResponseEntity<Resource> getImage(long id) {
        Optional<Usuario> userFound = repo.findById(id);
        if (!userFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String pathImagen = userFound.get().getImagen();
        Resource resource = null;
        if (pathImagen == null) {
            resource = new ClassPathResource("static/defaultUser/default.png");
        } else {
            Path path = Paths.get(pathImagen);
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
                throw new RuntimeException("Ha ocurrido un error al intentar obtener la imagen, el mensaje del error es: " + ex.getMessage());
            }
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }
}