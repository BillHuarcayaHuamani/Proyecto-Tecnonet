package com.ProyectoTecnonet.tecnonet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.model.Contrato;
import com.ProyectoTecnonet.tecnonet.model.EstadoContrato;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.EstadoContratoRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ContratoServiceImpl implements ContratoService {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @Autowired
    private FacturacionService facturacionService;

    @Override
    @Transactional
    public Contrato actualizarEstadoContrato(Integer idContrato, Integer nuevoEstadoId) {
        
        Contrato contrato = contratoRepository.findById(idContrato)
            .orElseThrow(() -> new EntityNotFoundException("Contrato no encontrado: " + idContrato));
        
        EstadoContrato nuevoEstado = estadoContratoRepository.findById(nuevoEstadoId)
            .orElseThrow(() -> new EntityNotFoundException("Estado no encontrado: " + nuevoEstadoId));

        Integer estadoOriginalId = contrato.getEstadoContrato().getIdEstadoContrato();

        contrato.setEstadoContrato(nuevoEstado);
        
        if (estadoOriginalId == 2 && nuevoEstadoId == 1) {
            System.out.println(">>> ContratoService: Transición de Pendiente a Activo detectada. Llamando a FacturacionService...");
            facturacionService.generarFacturasParaContrato(contrato);
        } else {
             System.out.println(">>> ContratoService: Transición no requiere generación de facturas. Estado original: " + estadoOriginalId + ", Nuevo estado: " + nuevoEstadoId);
        }

        return contratoRepository.save(contrato);
    }
}