package com.ProyectoTecnonet.tecnonet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RespuestaSolicitudDTO {
    @NotBlank
    private String respuesta;
}