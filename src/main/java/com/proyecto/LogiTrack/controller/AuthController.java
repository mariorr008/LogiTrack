
package com.proyecto.LogiTrack.controller;

import com.proyecto.LogiTrack.dto.request.LoginRequest;
import com.proyecto.LogiTrack.dto.request.RegistroRequest;
import com.proyecto.LogiTrack.dto.response.LoginResponse;
import com.proyecto.LogiTrack.dto.response.RegistroResponse;
import com.proyecto.LogiTrack.dto.response.EmpleadoPendienteDtoResponse;
import com.proyecto.LogiTrack.exception.ErrorResponse;
import com.proyecto.LogiTrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Inicio de sesión y registro de empleados.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y devuelve un JWT.")
    @RequestBody(
            required = true,
            description = "Credenciales de acceso.",
            content = @Content(
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(value = "{\"correo\":\"usuario@logitrack.com\",\"password\":\"ClaveSegura123\"}")
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso.",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta no activa.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> iniciarSesion(
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {

        LoginResponse response = authService.iniciarSesion(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar empleado", description = "Crea una cuenta de empleado pendiente de activación.")
    @RequestBody(
            required = true,
            description = "Datos requeridos para registrar al empleado.",
            content = @Content(
                    schema = @Schema(implementation = RegistroRequest.class),
                    examples = @ExampleObject(value = "{\"nombre\":\"Ana Pérez\",\"correo\":\"ana@logitrack.com\",\"telefono\":\"5551234567\",\"cedula\":\"ABC123456\",\"password\":\"ClaveSegura123\"}")
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empleado registrado.",
                    content = @Content(schema = @Schema(implementation = RegistroResponse.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o regla de negocio incumplida.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto con datos existentes.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<RegistroResponse> registrarEmpleado(
            @Valid @org.springframework.web.bind.annotation.RequestBody RegistroRequest request) {

        authService.registrarEmpleado(request);

        RegistroResponse response = new RegistroResponse(
                "Empleado registrado correctamente. La cuenta está pendiente de activación."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Listar empleados pendientes de activación
    @GetMapping("/pendientes")
    @Operation(summary = "Listar empleados pendientes", description = "Obtiene los empleados que esperan activación.")
    public ResponseEntity<List<EmpleadoPendienteDtoResponse>> listarEmpleadosPendientes() {

        List<EmpleadoPendienteDtoResponse> empleados =
                authService.listarEmpleadosPendientes();

        return ResponseEntity.ok(empleados);
    }

    // Activar un empleado
    @PutMapping("/{id}/activar")
    @Operation(summary = "Activar empleado", description = "Activa la cuenta de un empleado pendiente.")
    public ResponseEntity<Void> activarEmpleado(@PathVariable Integer id) {

        authService.activarEmpleado(id);

        return ResponseEntity.ok().build();
    }
}
