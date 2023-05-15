package com.api.portfolio.controllers;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import com.api.portfolio.services.IUsuarioService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {
    
    private final IUsuarioService userService;
    
    @GetMapping("/getUser/{id}")
    public ResponseEntity<Usuario> getUser(@PathVariable long id){
        return userService.getUser(id);
    }
    
    @PutMapping("/editTitulo/{id}")
    public ResponseEntity<?> putUserTitulo(@PathVariable long id,  @RequestBody String titulo){
        return userService.putUserTitulo(id, titulo);
    }
    
    
    @PutMapping("/editDescripcion/{id}")
    public ResponseEntity<?> putUserDescripcion(@PathVariable long id, @RequestBody String descripcion){
        return userService.putUserDescripcion(id, descripcion);
    }
    
    
    @GetMapping("/getUserImage/{id}")
    public ResponseEntity<?> getUserImagen(@PathVariable long id){
        return userService.getImage(id);
    }
    
    @PutMapping("/putUserImage/{id}")
    public ResponseEntity<?> putUserImagen(
            @PathVariable long id, 
            @RequestParam("imagen") MultipartFile imagen
    ) throws CreatingDirectoryImageException {
        return userService.actualizarImagenUsuario(id, imagen);
    }
    
    @PutMapping("putUserName/{id}")
    public ResponseEntity<?> putUserName(@PathVariable long id, @RequestBody String nuevoNombre){
        return userService.putUserName(id, nuevoNombre);
    }
    
    @PutMapping("putUserLastName/{id}")
    public ResponseEntity<?> putUserLastName(@PathVariable long id, @RequestBody String nuevoApellido){
        return userService.putUserLastName(id, nuevoApellido);
    }

    
    
}



    

