package com.api.portfolio.services;

import com.api.portfolio.entities.Educacion;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.repository.EducacionRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class EducacionService implements IEducacionService {

    private final EducacionRepository repo;
    private final IUsuarioService usuarioService;
    private final String root_folder = "src/main/resources/static/userImage/";

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
    public ResponseEntity<Resource> getImageEdu(long idEdu) {
        Optional<Educacion> found = repo.findById(idEdu);
        if (!found.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        String pathImage = found.get().getLogo();
        Resource resource = null;
        if (pathImage == null) {
            resource = new ClassPathResource("static/defaultEdu/default.png");
        } else {
            Path path = Paths.get(pathImage);
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Ha ocurrido un error al intentar obtener la imagen, Mesaje de error: " + e.getMessage());
            }
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }

    @Override
    public ResponseEntity<?> putImageEdu(long idEdu, MultipartFile imagen) throws CreatingDirectoryImageException {
        Optional<Educacion> found = repo.findById(idEdu);
        if (!found.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }
        Educacion educacion = found.get();
        if(educacion.getLogo() == null) {
            try {
                String nombreImagen = "edu_" + educacion.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                
                Path eduImageFolder = Paths.get(root_folder, "edu_image", Long.toString(educacion.getId()));
                Files.createDirectory(eduImageFolder);
                Path eduImagePath = eduImageFolder.resolve(nombreImagen);
                Files.write(eduImagePath, imagen.getBytes());
                
                educacion.setLogo(eduImagePath.toString());
                repo.save(educacion);
            } catch (IOException e) {
                throw new CreatingDirectoryImageException("No se pudo crear el directoria para la imagen. Mensaje de error: ", e);
            }
        } else {
            try {
                Path eduImagePath = Paths.get(educacion.getLogo());
                Files.write(eduImagePath, imagen.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo almacenar la imagen en el directorio");
            }
        }
        return ResponseEntity.ok().build();
    }

}
