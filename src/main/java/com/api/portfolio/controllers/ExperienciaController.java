package com.api.portfolio.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.portfolio.entities.Experiencia;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.services.IExperienciaService;

import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/exp")
@RequiredArgsConstructor
//@CrossOrigin(origins = "https://proyecto-5c92b.web.app")
public class ExperienciaController {
    
    private final IExperienciaService iExperiencia;


    @PutMapping("/edit/{idUsuario}/{idExperiencia}")
    public ResponseEntity<Experiencia> putExp(
            @PathVariable long idUsuario, 
            @PathVariable long idExperiencia,
            @RequestBody Experiencia experiencia
    ) {
        return iExperiencia.putExperience(idUsuario, idExperiencia, experiencia);
    }
    
    @PostMapping("/create/{idUsuario}/experiencia")
    public ResponseEntity<Experiencia> crearExperiencia(
            @PathVariable long idUsuario, 
            @RequestBody Experiencia experiencia
        ) {
        return iExperiencia.postExperience(idUsuario, experiencia);
    }
    
    @GetMapping("/getExperiences/{idUsuario}")
    public ResponseEntity<List<Experiencia>> getExperiences(@PathVariable long idUsuario){
        return iExperiencia.getExperiences(idUsuario);
    }
    
    @DeleteMapping("/deleteExp/{idExp}")
    public ResponseEntity<Experiencia> deleteExperience(@PathVariable long idExp){
        return iExperiencia.deleteExperience(idExp);
    }
    
    @GetMapping("/getImageExp/{idExp}")
    public ResponseEntity<?> getImageExperience(@PathVariable long idExp){
        return iExperiencia.getImageExpById(idExp);
    }
    
    @PutMapping("/putImageExp/{idExp}")
    public ResponseEntity<?> putImageExp(
            @PathVariable long idExp,
            @RequestPart ("imagen") MultipartFile imagen
    ) throws CreatingDirectoryImageException {
        return iExperiencia.putImageExp(idExp, imagen);
    }
}
