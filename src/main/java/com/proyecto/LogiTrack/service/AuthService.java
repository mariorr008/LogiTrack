
package com.proyecto.LogiTrack.service;

import com.proyecto.LogiTrack.dto.request.LoginRequest;
import com.proyecto.LogiTrack.dto.request.RegistroRequest;
import com.proyecto.LogiTrack.dto.response.LoginResponse;
import com.proyecto.LogiTrack.dto.response.EmpleadoPendienteDtoResponse;
import java.util.List;

public interface AuthService {

    LoginResponse iniciarSesion(LoginRequest request);

    void registrarEmpleado(RegistroRequest request);

    List<EmpleadoPendienteDtoResponse> listarEmpleadosPendientes();

    void activarEmpleado(Integer id);
}
