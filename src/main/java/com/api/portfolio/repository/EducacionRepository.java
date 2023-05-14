package com.api.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.portfolio.entities.Educacion;

@Repository
public interface EducacionRepository extends JpaRepository<Educacion, Long> {
    
}
