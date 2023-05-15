package com.api.portfolio.controllers;

import com.api.portfolio.entities.Educacion;
import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.services.IEducacionService;
import com.api.portfolio.services.IUsuarioService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/edu")
@RequiredArgsConstructor
@CrossOrigin("https://portfolio-arg-pro-9f98f.web.app")
public class EducacionController {

    private final IUsuarioService iUsuario;
    private final IEducacionService iEdu;

    @PutMapping("/edit/{idUsuario}/{idEducacion}")
    public ResponseEntity<Educacion> putExp(
            @PathVariable long idUsuario,
            @PathVariable long idEducacion,
            @RequestBody Educacion edu
    ) {
        Usuario usuario = iUsuario.getUser(idUsuario).getBody();
        edu.setUsuario(usuario);
        return iEdu.putEducation(idUsuario, idEducacion, edu);
    }

    @PostMapping("/create/{idUsuario}/educacion")
    public ResponseEntity<Educacion> crearExperiencia(
            @PathVariable long idUsuario,
            @RequestBody Educacion edu
    ) {
        Usuario usuario = iUsuario.getUser(idUsuario).getBody();
        edu.setUsuario(usuario);
        return iEdu.postEducation(idUsuario, edu);
    }

    @GetMapping("/getEducations/{idUsuario}")
    public ResponseEntity<List<Educacion>> getEducations(@PathVariable long idUsuario) {
        return iEdu.getEducations(idUsuario);
    }

    @PutMapping("/putImageEdu/{idEdu}")
    public ResponseEntity<?> putImageEdu(
            @PathVariable long idEdu,
            @RequestPart("imagen") MultipartFile imagen
    ) throws CreatingDirectoryImageException {
        return iEdu.putImageEdu(idEdu, imagen);
    }
    
    @DeleteMapping("delete/{idEdu}")
    public ResponseEntity<Educacion> deleteEduById(@PathVariable long idEdu){
        return iEdu.deleteEducation(idEdu);
    }
    
    @GetMapping("/getImageEdu/{idExp}")
    public ResponseEntity<?> getImageEdu(@PathVariable long idExp){
        return iEdu.getImageEdu(idExp);
    }

}
