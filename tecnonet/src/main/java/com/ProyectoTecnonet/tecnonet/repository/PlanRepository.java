package com.ProyectoTecnonet.tecnonet.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ProyectoTecnonet.tecnonet.model.Plan;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
}
