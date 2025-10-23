package com.ProyectoTecnonet.tecnonet.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PagoRequest {
    @NotBlank
    private String metodoPago;
}