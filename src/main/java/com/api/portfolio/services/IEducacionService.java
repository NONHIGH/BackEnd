package com.api.portfolio.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.api.portfolio.entities.Educacion;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IEducacionService {
    public ResponseEntity<Educacion> findById(long id);
    public ResponseEntity<Educacion> putEducation(long idUsuario, long idEdu, Educacion edu);
    public ResponseEntity<Educacion> deleteEducation(long idEdu);
    public ResponseEntity<List<Educacion>> getEducations(long idUsuario);
    public ResponseEntity<Educacion> postEducation(long idUsuario, Educacion edu);
    public ResponseEntity<Resource> getImageEdu(long id);
    public ResponseEntity<?> putImageEdu(long id, MultipartFile imagen) throws CreatingDirectoryImageException;
    

}
