
package com.proyecto.LogiTrack.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> crearRespuesta(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(error);
    }

    // Recurso no encontrado: 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarNoEncontrado(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // Regla de negocio incumplida: 400
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> manejarReglaNegocio(
            BusinessRuleException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Errores de validación de DTOs: 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Los datos enviados no son válidos");

        return crearRespuesta(
                HttpStatus.BAD_REQUEST, mensaje, request);
    }

    // Errores de parámetros: 400
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> manejarRestricciones(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // JSON mal formado: 400
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST,
                "El formato de los datos enviados no es válido",
                request);
    }

    // Conflictos con la integridad de la BD: 409
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarIntegridad(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.CONFLICT,
                "La operación genera un conflicto con los datos existentes",
                request);
    }

    // Otros errores de acceso a la BD: 500
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> manejarBaseDatos(
            DataAccessException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error al acceder a la base de datos",
                request);
    }

    // Excepciones que todavía utilizan nuestros Services: 400
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErrorResponse> manejarErroresNegocio(
            RuntimeException ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // Cualquier otro error inesperado: 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request) {

        return crearRespuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor",
                request);
    }
}
