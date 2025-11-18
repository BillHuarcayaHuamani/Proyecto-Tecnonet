package com.ProyectoTecnonet.tecnonet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.dto.AdminDashboardDTO;
import com.ProyectoTecnonet.tecnonet.service.DashboardService;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')") 
    public ResponseEntity<AdminDashboardDTO> getAdminDashboardData() {
        AdminDashboardDTO data = dashboardService.getAdminDashboardData();
        return ResponseEntity.ok(data);
    }
    
}