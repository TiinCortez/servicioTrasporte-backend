package com.tarifas.tarifa.repository;

import com.tarifas.tarifa.entities.Recargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecargoRepository extends JpaRepository<Recargo, Long> {
    // Buscar recargos por nombre (útil para evitar duplicados)
    List<Recargo> findByNombre(String nombre);
}