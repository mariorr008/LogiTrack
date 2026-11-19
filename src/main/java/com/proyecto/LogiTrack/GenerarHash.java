package com.proyecto.LogiTrack;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHash {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Admin123*";

        String hash = encoder.encode(password);

        System.out.println("Contraseña cifrada: " + hash);

        System.out.println("Ejecuta en base de datos lo siguiente:\n" +
                " UPDATE admin" +
                " SET password = "+ hash +
                " WHERE id = 100001;");
    }

}