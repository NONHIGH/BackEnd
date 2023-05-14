package com.api.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.portfolio.entities.Experiencia;

@Repository
public interface ExperienciaRepository extends JpaRepository<Experiencia, Long> {
    
}
