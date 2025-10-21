package com.ProyectoTecnonet.tecnonet.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ProyectoTecnonet.tecnonet.model.Plan;
import com.ProyectoTecnonet.tecnonet.repository.PlanRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/planes")
public class PlanController {

    @Autowired
    private PlanRepository planRepository;

    @GetMapping
    public List<Plan> getAllPlanes() {
        return planRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public Plan createPlan(@Valid @RequestBody Plan plan) {
        return planRepository.save(plan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public ResponseEntity<Plan> updatePlan(@PathVariable(value = "id") Integer planId, @Valid @RequestBody Plan planDetails) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con id :: " + planId));

        plan.setNombrePlan(planDetails.getNombrePlan());
        plan.setVelocidadDescargaMbps(planDetails.getVelocidadDescargaMbps());
        plan.setVelocidadCargaMbps(planDetails.getVelocidadCargaMbps());
        plan.setWifiIncluido(planDetails.getWifiIncluido());
        plan.setMesGratisPromocion(planDetails.getMesGratisPromocion());
        plan.setPuertosEthernet(planDetails.getPuertosEthernet());
        plan.setPrecioMensual(planDetails.getPrecioMensual());
        plan.setDescripcion(planDetails.getDescripcion());
        plan.setActivo(planDetails.getActivo());
        
        final Plan updatedPlan = planRepository.save(plan);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('OPERARIO')")
    public ResponseEntity<?> deletePlan(@PathVariable(value = "id") Integer planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado con id :: " + planId));

        planRepository.delete(plan);
        return ResponseEntity.ok().build();
    }
}
