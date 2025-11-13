package com.operaciones.operacion.repository;

import com.operaciones.operacion.entities.Camion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {
	// Buscar por disponibilidad
	List<Camion> findByDisponible(Boolean disponible);
}