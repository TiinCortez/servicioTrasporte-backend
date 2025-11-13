package com.tarifas.tarifa.repository;

import com.tarifas.tarifa.entities.TarifaBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TarifaBaseRepository extends JpaRepository<TarifaBase, Long> {

    // Esto será útil más adelante para buscar una tarifa por su nombre
    // Ej: "Tarifa General", "Tarifa Refrigerado"
    Optional<TarifaBase> findByNombre(String nombre);
}