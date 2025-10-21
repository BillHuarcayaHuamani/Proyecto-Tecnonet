package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.EstadoPago;
import com.ProyectoTecnonet.tecnonet.model.Factura;
import com.ProyectoTecnonet.tecnonet.repository.EstadoPagoRepository;
import com.ProyectoTecnonet.tecnonet.repository.FacturaRepository;

@Service
public class FacturacionService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private EstadoPagoRepository estadoPagoRepository;

    @Transactional
    public void generarFacturasParaContrato(Contrato contrato) {
        System.out
                .println(">>> FacturacionService: Iniciando generación para Contrato ID: " + contrato.getIdContrato());

        if (contrato == null || contrato.getPlan() == null || contrato.getEstadoContrato() == null) {
            System.err.println(">>> FacturacionService: Error - Contrato nulo o sin Plan/Estado cargado.");
            return;
        }
        System.out.println(">>> FacturacionService: Contrato válido. Estado: "
                + contrato.getEstadoContrato().getNombreEstado() + ", Plan: " + contrato.getPlan().getNombrePlan());

        if (contrato.getEstadoContrato().getIdEstadoContrato() != 1) {
            System.out.println(">>> FacturacionService: Contrato ID " + contrato.getIdContrato() + " no está activo ("
                    + contrato.getEstadoContrato().getNombreEstado() + "). No se generan facturas.");
            return;
        }

        long facturasExistentes = facturaRepository.countByContrato(contrato);
        System.out.println(">>> FacturacionService: Facturas existentes para este contrato: " + facturasExistentes);
        if (facturasExistentes > 0) {
            System.out.println(">>> FacturacionService: Ya existen facturas. No se generan nuevas.");
            return;
        }

        LocalDate fechaInicio = contrato.getFechaInicioServicio();
        LocalDate fechaFin = contrato.getFechaFinContrato();
        System.out.println(">>> FacturacionService: Fecha Inicio: " + fechaInicio + ", Fecha Fin: " + fechaFin);

        if (fechaInicio.isAfter(fechaFin)) {
            System.err.println(">>> FacturacionService: Error - Fecha de inicio es posterior a la fecha de fin.");
            return;
        }

        long numeroDeMeses = ChronoUnit.MONTHS.between(fechaInicio.withDayOfMonth(1), fechaFin.withDayOfMonth(1));
        if (fechaFin.getDayOfMonth() >= fechaInicio.getDayOfMonth() && !fechaFin.isBefore(fechaInicio)) {
        } else if (numeroDeMeses > 0 && fechaFin.getMonthValue() > fechaInicio.getMonthValue()) {
        } else {
            numeroDeMeses = Math.max(0, numeroDeMeses - 1);
        }

        System.out.println(">>> FacturacionService: Meses calculados para facturar: " + (numeroDeMeses + 1));

        EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                .orElseThrow(() -> new RuntimeException("Estado de pago 'Pendiente' no encontrado."));

        int facturasGeneradas = 0;
        for (int i = 0; i <= numeroDeMeses; i++) {
            LocalDate fechaEmisionFactura = fechaInicio.plusMonths(i);
            if (fechaEmisionFactura.isAfter(fechaFin)) {
                System.out.println(">>> FacturacionService: Omitiendo factura mes " + (i + 1) + " (fecha emisión "
                        + fechaEmisionFactura + " > fecha fin " + fechaFin + ")");
                continue;
            }
            System.out.println(">>> FacturacionService: Creando factura para mes " + (i + 1) + ", Fecha Emisión: "
                    + fechaEmisionFactura);

            Factura nuevaFactura = new Factura();
            nuevaFactura.setContrato(contrato);
            nuevaFactura.setEstadoPago(estadoPendiente);
            nuevaFactura.setMontoTotal(contrato.getPlan().getPrecioMensual());
            nuevaFactura.setFechaEmision(fechaEmisionFactura);
            nuevaFactura.setFechaVencimiento(fechaEmisionFactura.plusMonths(1).withDayOfMonth(10));
            nuevaFactura.setDescripcion("Factura Mes " + (i + 1) + " - Plan " + contrato.getPlan().getNombrePlan());

            facturaRepository.save(nuevaFactura);
            facturasGeneradas++;
        }
        System.out.println(">>> FacturacionService: Total facturas generadas: " + facturasGeneradas
                + " para contrato ID: " + contrato.getIdContrato());
    }

    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void actualizarFacturasVencidas() {
        System.out.println("Ejecutando tarea programada: Actualización de Facturas Vencidas...");
        LocalDate hoy = LocalDate.now();

        EstadoPago estadoVencido = estadoPagoRepository.findById(3)
                .orElseThrow(() -> new RuntimeException("Estado de pago 'Vencida' no encontrado."));

        List<Factura> facturasVencidas = facturaRepository.findFacturasPendientesVencidas(hoy);

        if (facturasVencidas.isEmpty()) {
            System.out.println("No se encontraron facturas pendientes vencidas para actualizar.");
        } else {
            for (Factura factura : facturasVencidas) {
                factura.setEstadoPago(estadoVencido);
                facturaRepository.save(factura);
                System.out.println("Factura ID: " + factura.getIdFactura() + " marcada como Vencida.");
            }
        }
        System.out.println("Actualización de Facturas Vencidas completada.");
    }
}