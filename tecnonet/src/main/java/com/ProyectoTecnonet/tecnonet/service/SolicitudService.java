package com.ProyectoTecnonet.tecnonet.service;

import com.ProyectoTecnonet.tecnonet.dto.SolicitudRequest;
import com.ProyectoTecnonet.tecnonet.model.Solicitudes;
import com.ProyectoTecnonet.tecnonet.model.Usuario;

public interface SolicitudService {
    Solicitudes guardarSolicitud(SolicitudRequest request, Usuario usuarioActual);
}