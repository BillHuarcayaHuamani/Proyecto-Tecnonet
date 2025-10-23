package com.ProyectoTecnonet.tecnonet.service;

import com.ProyectoTecnonet.tecnonet.dto.ContratoRequest;
import com.ProyectoTecnonet.tecnonet.model.Contrato;

public interface ContratoService {
    Contrato actualizarEstadoContrato(Integer idContrato, Integer nuevoEstadoId);
    
    Contrato crearContrato(ContratoRequest contratoRequest);
}