package com.ProyectoTecnonet.tecnonet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
public class FacturaDTO {
    private Integer idFactura;
    private Integer idContrato; 
    private String nombreEstadoPago;
    private BigDecimal montoTotal;
    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;
    private String metodoPago;
    private LocalDate fechaPago;
    private String descripcion;

    public FacturaDTO(com.ProyectoTecnonet.tecnonet.model.Factura factura) {
        this.idFactura = factura.getIdFactura();
        if (factura.getContrato() != null) { 
            this.idContrato = factura.getContrato().getIdContrato();
        }
        if (factura.getEstadoPago() != null) {
            this.nombreEstadoPago = factura.getEstadoPago().getNombreEstado();
        }
        this.montoTotal = factura.getMontoTotal();
        this.fechaEmision = factura.getFechaEmision();
        this.fechaVencimiento = factura.getFechaVencimiento();
        this.metodoPago = factura.getMetodoPago();
        this.fechaPago = factura.getFechaPago();
        this.descripcion = factura.getDescripcion();
    }
}