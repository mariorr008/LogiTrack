
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.LoginRequest;
import com.proyecto.LogiTrack.dto.request.RegistroRequest;
import com.proyecto.LogiTrack.dto.response.LoginResponse;
import com.proyecto.LogiTrack.dto.response.RegistroResponse;
import com.proyecto.LogiTrack.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.iniciarSesion(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegistroResponse> registrarEmpleado(
            @Valid @RequestBody RegistroRequest request) {

        authService.registrarEmpleado(request);

        RegistroResponse response = new RegistroResponse(
                "Empleado registrado correctamente. La cuenta está pendiente de activación."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
