package com.ProyectoTecnonet.tecnonet.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ProyectoTecnonet.tecnonet.dto.AdminDashboardDTO;
import com.ProyectoTecnonet.tecnonet.repository.ContratoRepository;
import com.ProyectoTecnonet.tecnonet.repository.FacturaRepository;
import com.ProyectoTecnonet.tecnonet.repository.SolicitudesRepository;
import com.ProyectoTecnonet.tecnonet.repository.UsuarioRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private SolicitudesRepository solicitudesRepository;

    @Override
    @Transactional(readOnly = true) 
    public AdminDashboardDTO getAdminDashboardData() {
        AdminDashboardDTO dto = new AdminDashboardDTO();

        LocalDate inicioMes = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        BigDecimal ingresos = facturaRepository.sumIngresosDelMes(2, inicioMes, finMes); 
        dto.setIngresosDelMes(ingresos != null ? ingresos : BigDecimal.ZERO);

        dto.setTotalClientesActivos(usuarioRepository.countByRolNombreRol("Cliente"));

        dto.setTotalContratosActivos(contratoRepository.countByEstadoContratoNombreEstado("Activo"));

        dto.setSolicitudesPendientes(solicitudesRepository.countByRespuestasSolicitudesIsNull());

        dto.setNuevosClientes(usuarioRepository.findTop5ByRolNombreRolOrderByFechaRegistroDesc("Cliente"));

        dto.setContratosPendientes(contratoRepository.findTop5ByEstadoContratoNombreEstadoOrderByFechaContratacionDesc("Pendiente de Activación"));

        return dto;
    }
}