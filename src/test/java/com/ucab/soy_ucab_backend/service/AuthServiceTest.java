package com.ucab.soy_ucab_backend.service;

import com.ucab.soy_ucab_backend.dto.auth.AuthResponseDto;
import com.ucab.soy_ucab_backend.dto.auth.LoginRequestDto;
import com.ucab.soy_ucab_backend.model.*;
import com.ucab.soy_ucab_backend.repository.MiembroRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private MiembroRepository miembroRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testLoginReturnsPersonaDetails() {
        Persona persona = new Persona();
        persona.setEmail("test@test.com");
        persona.setPassword("encodedPwd");
        persona.setRole(RoleEnum.usuario_estandar);
        persona.setFirstName("Juan");

        when(miembroRepository.findByEmail("test@test.com")).thenReturn(Optional.of(persona));
        when(passwordEncoder.matches("rawPwd", "encodedPwd")).thenReturn(true);

        LoginRequestDto request = new LoginRequestDto("test@test.com", "rawPwd");
        AuthResponseDto response = authService.login(request);

        assertEquals("Persona", response.getMemberType());
        assertEquals(persona, response.getMemberDetails());
    }

    @Test
    public void testLoginReturnsDependenciaUCABDetails() {
        DependenciaUCAB dep = new DependenciaUCAB();
        dep.setEmail("dep@ucab.edu.ve");
        dep.setPassword("encodedPwd");
        dep.setRole(RoleEnum.usuario_estandar);
        dep.setTipoEntidad(TipoEntidad.escuela);

        when(miembroRepository.findByEmail("dep@ucab.edu.ve")).thenReturn(Optional.of(dep));
        when(passwordEncoder.matches("rawPwd", "encodedPwd")).thenReturn(true);

        LoginRequestDto request = new LoginRequestDto("dep@ucab.edu.ve", "rawPwd");
        AuthResponseDto response = authService.login(request);

        assertEquals("DependenciaUCAB", response.getMemberType());
        assertEquals(dep, response.getMemberDetails());
    }
}
