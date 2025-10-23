package com.ProyectoTecnonet.tecnonet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.EstadoPago;
import com.ProyectoTecnonet.tecnonet.model.Factura;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.EstadoPagoRepository;
import com.ProyectoTecnonet.tecnonet.repository.FacturaRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class FacturacionService {

        @Autowired
        private FacturaRepository facturaRepository;

        @Autowired
        private ContratoRepository contratoRepository;

        @Autowired
        private EstadoPagoRepository estadoPagoRepository;

        @Transactional
        public Factura marcarComoPagada(Integer idFactura, String metodoPago) {
                System.out.println(">>> FacturacionService: Intentando marcar como pagada la Factura ID: " + idFactura);

                Factura factura = facturaRepository.findById(idFactura)
                                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada: " + idFactura));

                Integer estadoActualId = factura.getEstadoPago().getIdEstadoPago();

                if (estadoActualId != 1 && estadoActualId != 3) {
                        throw new IllegalStateException("La factura no se puede pagar porque su estado actual es: "
                                        + factura.getEstadoPago().getNombreEstado());
                }

                EstadoPago estadoPagada = estadoPagoRepository.findById(2) 
                                .orElseThrow(() -> new RuntimeException(
                                                "Estado de pago 'Pagada' (ID 2) no encontrado."));

                factura.setEstadoPago(estadoPagada);
                factura.setFechaPago(LocalDate.now());
                factura.setMetodoPago(metodoPago);

                facturaRepository.save(factura);
                System.out.println(">>> FacturacionService: Factura ID " + idFactura
                                + " marcada como Pagada con método: " + metodoPago);

                return facturaRepository.findByIdCompleta(idFactura)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Factura no encontrada después de guardar: " + idFactura)); 
                                                                                                            
        }

        @Transactional
        public void generarFacturasPendientesAlActivar(Contrato contrato) {
                System.out.println(
                                ">>> FacturacionService: Iniciando generación de facturas 'catch-up' para Contrato ID: "
                                                + contrato.getIdContrato());

                LocalDate hoy = LocalDate.now();
                LocalDate fechaInicioServicio = contrato.getFechaInicioServicio();

                EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                                .orElseThrow(() -> new RuntimeException(
                                                "Estado de pago 'Pendiente' (ID 1) no encontrado."));

                if (contrato.getCostoInstalacion() != null
                                && contrato.getCostoInstalacion().compareTo(BigDecimal.ZERO) > 0) {
                        Factura facturaInstalacion = new Factura();
                        facturaInstalacion.setContrato(contrato);
                        facturaInstalacion.setEstadoPago(estadoPendiente);
                        facturaInstalacion.setMontoTotal(contrato.getCostoInstalacion());
                        facturaInstalacion.setFechaEmision(hoy);
                        facturaInstalacion.setFechaVencimiento(hoy.plusDays(10));
                        facturaInstalacion.setDescripcion(
                                        "Costo de Instalación - Contrato ID " + contrato.getIdContrato());
                        facturaRepository.save(facturaInstalacion);
                        System.out.println(
                                        ">>> FacturacionService: Generada factura de instalación por: "
                                                        + contrato.getCostoInstalacion());
                }

                if (fechaInicioServicio.isAfter(hoy)) {
                        System.out.println(
                                        ">>> FacturacionService: El inicio del servicio es en el futuro. No se generan facturas mensuales 'catch-up'.");
                        return;
                }

                LocalDate fechaFactura = fechaInicioServicio;
                int facturasGeneradas = 0;

                while (!fechaFactura.isAfter(hoy)) {

                        LocalDate inicioMes = fechaFactura.withDayOfMonth(1);
                        LocalDate finMes = fechaFactura.withDayOfMonth(fechaFactura.lengthOfMonth());
                        boolean yaFacturado = facturaRepository.existsByContratoAndFechaEmisionBetween(contrato,
                                        inicioMes, finMes);

                        if (!yaFacturado) {
                                BigDecimal montoAFacturar;
                                String descripcionFactura;

                                if (facturasGeneradas == 0 && fechaFactura.getDayOfMonth() != 1) {
                                        System.out.println(
                                                        ">>> FacturacionService: Calculando prorrateo para el primer mes...");
                                        BigDecimal precioMensual = contrato.getPlan().getPrecioMensual();
                                        int diasEnMes = fechaFactura.lengthOfMonth();
                                        int diasUsados = diasEnMes - fechaFactura.getDayOfMonth() + 1;

                                        BigDecimal precioDiario = precioMensual.divide(new BigDecimal(diasEnMes), 4,
                                                        RoundingMode.HALF_UP);
                                        montoAFacturar = precioDiario.multiply(new BigDecimal(diasUsados)).setScale(2,
                                                        RoundingMode.HALF_UP);

                                        descripcionFactura = "Factura Prorrateada (" + diasUsados + " días) - "
                                                        + contrato.getPlan().getNombrePlan();

                                } else {
                                        montoAFacturar = contrato.getPlan().getPrecioMensual();
                                        descripcionFactura = "Factura Mes " + fechaFactura.getMonthValue() + "/"
                                                        + fechaFactura.getYear()
                                                        + " - " + contrato.getPlan().getNombrePlan();
                                }

                                System.out.println(
                                                ">>> FacturacionService: Generando factura " + descripcionFactura
                                                                + " por " + montoAFacturar);

                                Factura nuevaFactura = new Factura();
                                nuevaFactura.setContrato(contrato);
                                nuevaFactura.setEstadoPago(estadoPendiente);
                                nuevaFactura.setMontoTotal(montoAFacturar);
                                nuevaFactura.setFechaEmision(fechaFactura);
                                nuevaFactura.setFechaVencimiento(fechaFactura.plusDays(10));
                                nuevaFactura.setDescripcion(descripcionFactura);

                                facturaRepository.save(nuevaFactura);
                                facturasGeneradas++;
                        }

                        fechaFactura = fechaFactura.plusMonths(1).withDayOfMonth(1);
                }

                System.out.println(
                                ">>> FacturacionService: Generación 'catch-up' completada. Total facturas mensuales generadas: "
                                                + facturasGeneradas);
        }

        @Scheduled(cron = "0 15 20 * * ?")
        @Transactional
        public void generarFacturasMensuales() {
                System.out.println(">>> TAREA PROGRAMADA: Iniciando generación de facturas mensuales...");

                LocalDate hoy = LocalDate.now();
                int diaHoy = hoy.getDayOfMonth();

                List<Contrato> contratosAFacturar = contratoRepository.findContratosActivosParaFacturar(1, diaHoy);

                if (contratosAFacturar.isEmpty()) {
                        System.out.println(
                                        ">>> TAREA PROGRAMADA: No hay contratos para facturar hoy (" + diaHoy + ").");
                        return;
                }

                EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                                .orElseThrow(() -> new RuntimeException(
                                                "Estado de pago 'Pendiente' (ID 1) no encontrado."));

                for (Contrato contrato : contratosAFacturar) {

                        if (hoy.isAfter(contrato.getFechaFinContrato())) {
                                continue;
                        }

                        LocalDate inicioMes = hoy.withDayOfMonth(1);
                        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
                        boolean yaFacturado = facturaRepository.existsByContratoAndFechaEmisionBetween(contrato,
                                        inicioMes, finMes);

                        if (yaFacturado) {
                                continue;
                        }

                        System.out.println(">>> TAREA PROGRAMADA: Generando factura para Contrato ID: "
                                        + contrato.getIdContrato());
                        Factura nuevaFactura = new Factura();
                        nuevaFactura.setContrato(contrato);
                        nuevaFactura.setEstadoPago(estadoPendiente);
                        nuevaFactura.setMontoTotal(contrato.getPlan().getPrecioMensual());
                        nuevaFactura.setFechaEmision(hoy);
                        nuevaFactura.setFechaVencimiento(hoy.plusDays(10));

                        nuevaFactura.setDescripcion("Factura Mes " + hoy.getMonthValue() + "/" + hoy.getYear() + " - "
                                        + contrato.getPlan().getNombrePlan());

                        facturaRepository.save(nuevaFactura);
                }

                System.out.println(">>> TAREA PROGRAMADA: Generación de facturas mensuales completada.");
        }

        @Scheduled(cron = "0 16 20 * * ?")
        @Transactional
        public void actualizarFacturasVencidas() {
                System.out.println(">>> TAREA PROGRAMADA: Actualización de Facturas Vencidas...");
                LocalDate hoy = LocalDate.now();

                EstadoPago estadoVencido = estadoPagoRepository.findById(3)
                                .orElseThrow(() -> new RuntimeException("Estado de pago 'Vencida' no encontrado."));

                List<Factura> facturasVencidas = facturaRepository.findFacturasPendientesVencidas(hoy);

                if (facturasVencidas.isEmpty()) {
                        System.out.println(
                                        ">>> TAREA PROGRAMADA: No se encontraron facturas pendientes vencidas para actualizar.");
                } else {
                        for (Factura factura : facturasVencidas) {
                                factura.setEstadoPago(estadoVencido);
                                facturaRepository.save(factura);
                                System.out.println(
                                                ">>> TAREA PROGRAMADA: Factura ID: " + factura.getIdFactura()
                                                                + " marcada como Vencida.");
                        }
                }
                System.out.println(">>> TAREA PROGRAMADA: Actualización de Facturas Vencidas completada.");
        }

        @Transactional
        public void anularFacturasFuturas(Contrato contrato) {
                System.out.println(">>> FacturacionService: Iniciando anulación de facturas futuras para Contrato ID: "
                                + contrato.getIdContrato());
                LocalDate hoy = LocalDate.now();

                EstadoPago estadoAnulada = estadoPagoRepository.findById(4)
                                .orElseThrow(() -> new RuntimeException(
                                                "Estado de pago 'Anulada' (ID 4) no encontrado."));

                List<Factura> facturasFuturas = facturaRepository.findFacturasFuturasPendientesOVencidas(contrato, hoy);

                if (facturasFuturas.isEmpty()) {
                        System.out.println(
                                        ">>> FacturacionService: No se encontraron facturas futuras pendientes/vencidas para anular.");
                        return;
                }

                for (Factura factura : facturasFuturas) {
                        System.out.println(">>> FacturacionService: Anulando Factura ID: " + factura.getIdFactura()
                                        + " (Fecha Emisión: " + factura.getFechaEmision() + ")");
                        factura.setEstadoPago(estadoAnulada);
                        facturaRepository.save(factura);
                }
                System.out.println(
                                ">>> FacturacionService: Anulación de " + facturasFuturas.size()
                                                + " facturas futuras completada.");
        }

        @Transactional
        public void prorratearFacturaMesActual(Contrato contrato, LocalDate fechaTerminacion) {
                System.out.println(
                                ">>> FacturacionService: Intentando prorratear/generar factura del mes actual para Contrato ID: "
                                                + contrato.getIdContrato());

                LocalDate inicioMes = fechaTerminacion.withDayOfMonth(1);
                LocalDate finMes = fechaTerminacion.withDayOfMonth(fechaTerminacion.lengthOfMonth());

                boolean facturaDelMesExiste = facturaRepository.existsByContratoAndFechaEmisionBetween(contrato,
                                inicioMes,
                                finMes);

                if (facturaDelMesExiste) {
                        Factura facturaMesActualPendiente = facturaRepository
                                        .findFacturaPendienteDelMes(contrato, inicioMes, finMes)
                                        .orElse(null);

                        if (facturaMesActualPendiente != null) {
                                System.out.println(">>> FacturacionService: Factura pendiente encontrada (ID: "
                                                + facturaMesActualPendiente.getIdFactura()
                                                + "). Actualizando monto prorrateado...");

                                int diasActivos = fechaTerminacion.getDayOfMonth();
                                int diasTotalesMes = fechaTerminacion.lengthOfMonth();
                                if (diasTotalesMes == 0)
                                        return;

                                BigDecimal precioMensualOriginal = contrato.getPlan().getPrecioMensual();
                                BigDecimal montoProrrateado = precioMensualOriginal
                                                .multiply(new BigDecimal(diasActivos))
                                                .divide(new BigDecimal(diasTotalesMes), 2, RoundingMode.HALF_UP);

                                System.out.println(
                                                ">>> FacturacionService: Prorrateando Factura ID: "
                                                                + facturaMesActualPendiente.getIdFactura()
                                                                + ". Días activos: " + diasActivos + "/"
                                                                + diasTotalesMes
                                                                + ". Monto original: " + precioMensualOriginal
                                                                + ". Monto prorrateado: " + montoProrrateado);

                                facturaMesActualPendiente.setMontoTotal(montoProrrateado);
                                facturaMesActualPendiente
                                                .setDescripcion(facturaMesActualPendiente.getDescripcion()
                                                                + " (Prorrateado por terminación)");
                                facturaRepository.save(facturaMesActualPendiente);

                        } else {
                                System.out.println(
                                                ">>> FacturacionService: Ya existe una factura para este mes, pero no está pendiente. No se prorratea.");
                        }

                } else {
                        System.out.println(
                                        ">>> FacturacionService: No existe factura para este mes. Creando nueva factura prorrateada...");

                        int diasActivos = fechaTerminacion.getDayOfMonth();
                        int diasTotalesMes = fechaTerminacion.lengthOfMonth();
                        if (diasTotalesMes == 0)
                                return;

                        BigDecimal precioMensualOriginal = contrato.getPlan().getPrecioMensual();
                        BigDecimal montoProrrateado = precioMensualOriginal
                                        .multiply(new BigDecimal(diasActivos))
                                        .divide(new BigDecimal(diasTotalesMes), 2, RoundingMode.HALF_UP);

                        EstadoPago estadoPendiente = estadoPagoRepository.findById(1)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Estado de pago 'Pendiente' (ID 1) no encontrado."));

                        System.out.println(">>> FacturacionService: Creando factura prorrateada. Días activos: "
                                        + diasActivos + "/"
                                        + diasTotalesMes
                                        + ". Monto: " + montoProrrateado);

                        Factura nuevaFactura = new Factura();
                        nuevaFactura.setContrato(contrato);
                        nuevaFactura.setEstadoPago(estadoPendiente);
                        nuevaFactura.setMontoTotal(montoProrrateado);
                        nuevaFactura.setFechaEmision(fechaTerminacion);
                        nuevaFactura.setFechaVencimiento(fechaTerminacion.plusDays(10));
                        nuevaFactura
                                        .setDescripcion("Factura Mes " + fechaTerminacion.getMonthValue() + "/"
                                                        + fechaTerminacion.getYear()
                                                        + " - " + contrato.getPlan().getNombrePlan()
                                                        + " (Prorrateado por terminación)");

                        facturaRepository.save(nuevaFactura);
                }
        }
}