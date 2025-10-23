package com.ProyectoTecnonet.tecnonet.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.dto.SolicitudRequest;
import com.ProyectoTecnonet.tecnonet.model.Solicitudes;
import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.repository.SolicitudesRepository;

@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private SolicitudesRepository solicitudesRepository;
    
    @Override
    @Transactional
    public Solicitudes guardarSolicitud(SolicitudRequest request, Usuario usuarioActual) {
        
        Solicitudes nuevaSolicitud = new Solicitudes();
        
        if (usuarioActual != null) {
            nuevaSolicitud.setUsuario(usuarioActual);
            nuevaSolicitud.setNumeroRemitente(request.getTelefonoRemitente());
            nuevaSolicitud.setApellidoRemitente(usuarioActual.getApellido());
        } else {
             nuevaSolicitud.setApellidoRemitente(request.getApellidoRemitente());
             nuevaSolicitud.setNumeroRemitente(request.getTelefonoRemitente());
             Usuario usuarioAnonimo = new Usuario(); 
             usuarioAnonimo.setIdUsuario(0);
             nuevaSolicitud.setUsuario(usuarioAnonimo); 
             System.out.println(">>> SolicitudService: Usuario no logueado. Asociando a usuario genérico ID 0.");
        }

        nuevaSolicitud.setAsunto(request.getAsunto());
        nuevaSolicitud.setMensaje(request.getMensaje());
        nuevaSolicitud.setTelefono_remitente(request.getTelefonoRemitente());
        
        nuevaSolicitud.setFechaEnvio(LocalDateTime.now());

        System.out.println(">>> SolicitudService: Guardando nueva solicitud...");
        return solicitudesRepository.save(nuevaSolicitud);
    }
}