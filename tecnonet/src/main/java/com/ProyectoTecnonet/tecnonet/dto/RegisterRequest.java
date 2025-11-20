package com.ProyectoTecnonet.tecnonet.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Integer idRol; 
}