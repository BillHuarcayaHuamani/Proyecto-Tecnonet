package com.ProyectoTecnonet.tecnonet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SolicitudRequest {

    @Size(max = 100)
    private String nombreRemitente;

    @Size(max = 100)
    private String apellidoRemitente;

    @Email
    @Size(max = 255)
    private String correoRemitente;

    @Size(max = 20)
    private String telefonoRemitente; 

    @NotBlank
    @Size(max = 255)
    private String asunto;

    @NotBlank
    private String mensaje;

}