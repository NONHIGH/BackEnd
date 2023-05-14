
package com.api.portfolio.dto;

import com.api.portfolio.entities.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UsuarioImagenDTO {
    private Usuario usuario;
    private String imagen;
}
