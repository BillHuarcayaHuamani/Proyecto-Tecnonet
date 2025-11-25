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

        if (estadoOriginalId.equals(nuevoEstadoId)) return contrato;

        contrato.setEstadoContrato(nuevoEstado);
        
        if (estadoOriginalId == 2 && nuevoEstadoId == 1) { 
            System.out.println(">>> ContratoService: Activando contrato...");
            contrato.setFechaActivacion(LocalDate.now());
            facturacionService.generarFacturasPendientesAlActivar(contrato); 
        } 
        else if (estadoOriginalId == 1 && (nuevoEstadoId == 3 || nuevoEstadoId == 4)) { 
            System.out.println(">>> ContratoService: Terminando contrato...");
            LocalDate fechaTerminacion = LocalDate.now();
            facturacionService.prorratearFacturaMesActual(contrato, fechaTerminacion);
            facturacionService.anularFacturasFuturas(contrato);
        }

        return contratoRepository.save(contrato);
    }

    @Override
    @Transactional
    public Contrato crearContrato(ContratoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
            .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        Plan plan = planRepository.findById(request.getIdPlan())
            .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));
        EstadoContrato estadoPendiente = estadoContratoRepository.findById(2)
            .orElseThrow(() -> new EntityNotFoundException("Estado 'Pendiente' no encontrado"));

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

    @Override
    @Transactional
    public Contrato actualizarDatosContrato(Integer idContrato, ContratoRequest request) {
        Contrato contrato = contratoRepository.findByIdWithDetails(idContrato)
            .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado"));
            
        Plan nuevoPlan = planRepository.findById(request.getIdPlan())
             .orElseThrow(() -> new EntityNotFoundException("Plan no encontrado"));
        
        contrato.setPlan(nuevoPlan);
        
        contrato.setDireccionInstalacion(request.getDireccionInstalacion());
        contrato.setNumeroTelefonoContacto(request.getNumeroTelefonoContacto());
        contrato.setMetodoPago(request.getMetodoPago());
        contrato.setObservaciones(request.getObservaciones());
        
        if(request.getFechaInicioServicio() != null) {
             contrato.setFechaInicioServicio(LocalDate.parse(request.getFechaInicioServicio()));
             contrato.setFechaFinContrato(LocalDate.parse(request.getFechaFinContrato()));
        }

        return contratoRepository.save(contrato);
    }
}