package com.api.portfolio.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.api.portfolio.entities.Experiencia;
import com.api.portfolio.exceptions.createDirectory.CreatingDirectoryImageException;
import org.springframework.web.multipart.MultipartFile;

public interface IExperienciaService {
    public ResponseEntity<Experiencia> findById(long id);
    public ResponseEntity<Experiencia> putExperience(long idUsuario, long idExperiencia, Experiencia experiencia);
    public ResponseEntity<Experiencia> deleteExperience(long id);
    public ResponseEntity<List<Experiencia>> getExperiences(long idUsuario);
    public ResponseEntity<Experiencia> postExperience(long idUsuario,Experiencia experiencia);
    public ResponseEntity<?> getImageExpById(long id);
    public ResponseEntity<?> putImageExp(long id, MultipartFile imagen) throws CreatingDirectoryImageException;
}
