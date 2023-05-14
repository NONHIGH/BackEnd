package com.api.portfolio.services;

import com.api.portfolio.entities.Experiencia;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.repository.ExperienciaRepository;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class ExperienciaService implements IExperienciaService {
    
    private final ExperienciaRepository repo;
    private final IUsuarioService usuarioService;
    
    private final String root_folder = "src/main/resources/static/userImage/";
    
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
    public ResponseEntity<Resource> getImageExpById(long idExp) {
        Optional<Experiencia> expFound = repo.findById(idExp);
        if (!expFound.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        String pathImagen = expFound.get().getLogoEmpresa();
        Resource resource = null;
        if (pathImagen == null) {
            resource = new ClassPathResource("static/defaultExp/default.png");
        } else {
            Path path = Paths.get(pathImagen);
            try {
                resource = new UrlResource(path.toUri());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Ha ocurrido un error al intentar obtener la imagen, el mensaje de error es: " + e.getMessage());
            }
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(resource);
    }
    
    @Override
    public ResponseEntity<?> putImageExp(long id, MultipartFile imagen) throws CreatingDirectoryImageException {
        Optional<Experiencia> found = repo.findById(id);
        if (!found.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Experiencia experiencia = found.get();
        if (experiencia.getLogoEmpresa() == null) {
            try {
                String nombreImagen = "exp_" + experiencia.getId() + "_" + UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(imagen.getOriginalFilename());
                
                Path expImageFolder = Paths.get(root_folder, "exp_image", Long.toString(experiencia.getId()));
                Files.createDirectories(expImageFolder);
                Path expImagePath = expImageFolder.resolve(nombreImagen);
                Files.write(expImagePath, imagen.getBytes());
                
                experiencia.setLogoEmpresa(expImagePath.toString());
                repo.save(experiencia);
            } catch (IOException e) {
                throw new CreatingDirectoryImageException("No se pudo crear el directorio para la imagen. Mensaje de error: ", e);
            }
        } else {
            try {
                Path expImagePath = Paths.get(experiencia.getLogoEmpresa());
                Files.write(expImagePath, imagen.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No se pudo almacenar la imagen en el directorio");
            }
        }
        return ResponseEntity.ok().build();
    }
    
}
