package com.api.portfolio.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.api.portfolio.entities.Usuario;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import org.springframework.web.multipart.MultipartFile;

public interface IUsuarioService {
    
    public ResponseEntity<Usuario> getUser(long id);
    
    public ResponseEntity<Usuario> putUser(long id, Usuario usuario);
    
    public ResponseEntity<?> putUserTitulo(long id, String titulo);
    
    public ResponseEntity<?> putUserDescripcion(long id, String descripcion);
    
    public ResponseEntity<?> putUserName(long id, String nuevoNombre);
    
    public ResponseEntity<?> putUserLastName(long id, String nuevoApellido);
    
    //este metodo sera aplicado para un usuario de tipo admin
    public ResponseEntity<List<Usuario>> getUsers();
    
    //metodos que se podran acceder desde una cuenta de usaurio
    public ResponseEntity<?> getImage(long id);
    
    public ResponseEntity<?> actualizarImagenUsuario(long id, MultipartFile imagen) throws CreatingDirectoryImageException;
    
    
}
