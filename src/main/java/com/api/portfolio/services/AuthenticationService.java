package com.api.portfolio.services;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.api.portfolio.entities.Rol;
import com.api.portfolio.entities.Usuario;
import com.api.portfolio.entities.authentication.AuthenticationRequest;
import com.api.portfolio.entities.authentication.AuthenticationResponse;
import com.api.portfolio.entities.authentication.RegisterRequest;
import com.api.portfolio.exceptions.domain.EmailAlreadyExistsException;
import com.api.portfolio.repository.UsuarioRepository;
import com.api.portfolio.security.jwt.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    
    private final UsuarioRepository usuarioRepository;

    private final AuthenticationManager authenticationManager;
    

    public AuthenticationResponse register(RegisterRequest request) throws EmailAlreadyExistsException{
        if(usuarioRepository.existsByEmail(request.getEmail())){
            throw new EmailAlreadyExistsException("El email ya existe, intenta con otro");
        }
        var user = Usuario.builder()
                        .nombre(request.getNombre())
                        .apellido(request.getApellido())
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .rol(Rol.USER)
                        .experiencias(null)
                        .educaciones(null)
                        .imagen(null)
                        .descripcion(null)
                        .titulo(null)
                        .build();
        usuarioRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                                        .token(jwtToken)
                                        .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), 
                                    request.getPassword()
                                )
        );
        var user = usuarioRepository
                    .findByEmail(request.getEmail())
                    .orElseThrow(() -> {
            return new UsernameNotFoundException("Usuario no encontrado");
        });
        
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                                        .token(jwtToken)
                                        .idUsuario(user.getId())
                                        .build();
    }

 
    

}
