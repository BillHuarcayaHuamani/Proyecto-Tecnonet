package com.ProyectoTecnonet.tecnonet.dto;

import java.math.BigDecimal;
import java.util.List;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.Usuario;

import lombok.Data;

@Data
public class AdminDashboardDTO {

    private BigDecimal ingresosDelMes;
    private long totalClientesActivos;
    private long totalContratosActivos;
    private long solicitudesPendientes;

    private List<Usuario> nuevosClientes;
    private List<Contrato> contratosPendientes;
}