package com.api.portfolio.controllers;

import com.api.portfolio.entities.Educacion;
import com.api.portfolio.entities.Experiencia;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.entities.authentication.AuthenticationRequest;
import com.api.portfolio.entities.authentication.AuthenticationResponse;
import com.api.portfolio.entities.authentication.RegisterRequest;
import com.api.portfolio.exceptions.domain.EmailAlreadyExistsException;
import com.api.portfolio.exceptions.domain.ErrorResponse;
import com.api.portfolio.repository.UsuarioRepository;
import com.api.portfolio.services.AuthenticationService;
import com.api.portfolio.services.IEducacionService;
import com.api.portfolio.services.IExperienciaService;
import com.api.portfolio.services.IUsuarioService;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/open")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {

    private final UsuarioRepository repo;
    private final AuthenticationService authenticationService;
    private final IUsuarioService userService;
    private final IExperienciaService expService;
    private final IEducacionService eduService;


    @PostMapping("/register")
    public ResponseEntity<?> registrar(
            @RequestBody RegisterRequest request
        )
    {
        try{
            AuthenticationResponse response = authenticationService.register(request);
            return ResponseEntity.ok(response);
        }catch(EmailAlreadyExistsException e){
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Ese email ya existe"));
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> autenticar(
            @RequestBody AuthenticationRequest request
        )
    {
        try {
            AuthenticationResponse response = authenticationService.authenticate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("Credenciales de usuario incorrectas"));
    }
    }

        @GetMapping("/main")
        public ResponseEntity<Optional<Usuario>> pageMain() {
            return ResponseEntity.ok()
                    .body(repo.findById(1L));
        }
        
        @GetMapping("/getUserImage")
        public ResponseEntity<?> getUserImagen(){
            return userService.getImage(1L);
        }
        
        @GetMapping("/mainExp")
        public ResponseEntity<List<Experiencia>> getExp(){
            return expService.getExperiences(1);
        }
        
        @GetMapping("/mainImageExp/{idExp}")
        public ResponseEntity<?> getImageExp(@PathVariable long idExp){
            return expService.getImageExpById(idExp);
        }
        
        @GetMapping("/mainEdu")
        public ResponseEntity<List<Educacion>> getEdu(){
            return eduService.getEducations(1);
        }
        
        @GetMapping("/mainImageEdu/{idEdu}")
        public ResponseEntity<?> getImageEdu(@PathVariable long idEdu){
            return eduService.getImageEdu(idEdu);
        }
}
