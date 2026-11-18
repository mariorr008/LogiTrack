package com.proyecto.LogiTrack.dto.response;

public record EmpleadoPendienteDtoResponse(
        Integer id,
        String nombre,
        String correo,
        String telefono,
        String cedula,
        String estado
) {}