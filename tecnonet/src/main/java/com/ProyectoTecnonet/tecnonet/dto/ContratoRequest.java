package com.ProyectoTecnonet.tecnonet.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ContratoRequest {
    private Integer idUsuario;
    private Integer idPlan;
    private String fechaContratacion;
    private String fechaInicioServicio;
    private String fechaFinContrato;
    private String direccionInstalacion;
    private String numeroTelefonoContacto;
    private String metodoPago;
    private BigDecimal costoInstalacion;
    private String observaciones;
}