package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.dto.ContratoRequest;
import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.EstadoContrato;
import com.ProyectoTecnonet.tecnonet.model.Plan;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.EstadoContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.PlanRepository;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @Autowired
    private FacturacionService facturacionService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PlanRepository planRepository;

    @Override
    @Transactional
    public Contrato actualizarEstadoContrato(Integer idContrato, Integer nuevoEstadoId) {
        
        Contrato contrato = contratoRepository.findByIdWithDetails(idContrato)
            .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado: " + idContrato));
        
        EstadoContrato nuevoEstado = estadoContratoRepository.findById(nuevoEstadoId)
            .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + nuevoEstadoId));

        Integer estadoOriginalId = contrato.getEstadoContrato().getIdEstadoContrato();

        if (estadoOriginalId.equals(nuevoEstadoId)) {
            return contrato;
        }

        contrato.setEstadoContrato(nuevoEstado);
        
        if (estadoOriginalId == 2 && nuevoEstadoId == 1) { 
            System.out.println(">>> ContratoService: Transición de Pendiente a Activo. Generando facturas 'catch-up'...");
            facturacionService.generarFacturasPendientesAlActivar(contrato); 
        
        } else if (estadoOriginalId == 1 && (nuevoEstadoId == 3 || nuevoEstadoId == 4)) { 
            System.out.println(">>> ContratoService: Transición de Activo a Finalizado/Cancelado. Prorrateando mes actual y anulando futuras...");
            LocalDate fechaTerminacion = LocalDate.now();
            
            facturacionService.prorratearFacturaMesActual(contrato, fechaTerminacion);
            
            facturacionService.anularFacturasFuturas(contrato);

        } else {
             System.out.println(">>> ContratoService: Transición no requiere acción especial de facturación. Estado original: " + estadoOriginalId + ", Nuevo estado: " + nuevoEstadoId);
        }

        return contratoRepository.save(contrato);
    }

    @Override
    @Transactional
    public Contrato crearContrato(ContratoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + request.getIdUsuario()));
        
        Plan plan = planRepository.findById(request.getIdPlan())
            .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado: " + request.getIdPlan()));
        
        EstadoContrato estadoPendiente = estadoContratoRepository.findById(2)
            .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente de Activación' (ID=2) no encontrado"));

        Contrato nuevoContrato = new Contrato();
        nuevoContrato.setUsuario(usuario);
        nuevoContrato.setPlan(plan);
        nuevoContrato.setEstadoContrato(estadoPendiente);
        
        nuevoContrato.setFechaContratacion(LocalDate.parse(request.getFechaContratacion()));
        nuevoContrato.setFechaInicioServicio(LocalDate.parse(request.getFechaInicioServicio()));
        nuevoContrato.setFechaFinContrato(LocalDate.parse(request.getFechaFinContrato()));
        nuevoContrato.setDireccionInstalacion(request.getDireccionInstalacion());
        nuevoContrato.setNumeroTelefonoContacto(request.getNumeroTelefonoContacto());
        nuevoContrato.setMetodoPago(request.getMetodoPago());
        nuevoContrato.setCostoInstalacion(request.getCostoInstalacion());
        nuevoContrato.setObservaciones(request.getObservaciones());

        return contratoRepository.save(nuevoContrato);
    }
}