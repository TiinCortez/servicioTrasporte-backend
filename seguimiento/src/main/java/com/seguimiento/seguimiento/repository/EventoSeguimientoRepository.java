package com.seguimiento.seguimiento.repository;

import com.seguimiento.seguimiento.entities.EventoSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoSeguimientoRepository extends JpaRepository<EventoSeguimiento, Long> {

    // Para la línea de tiempo ordenada cronológicamente
    List<EventoSeguimiento> findBySolicitudIdOrderByFechaHoraAsc(Long solicitudId);
}
