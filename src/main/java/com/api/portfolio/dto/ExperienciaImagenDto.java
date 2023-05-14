package com.api.portfolio.dto;

import com.api.portfolio.entities.Experiencia;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExperienciaImagenDto {
    private Experiencia experiencia;
    private String imagen;
}
