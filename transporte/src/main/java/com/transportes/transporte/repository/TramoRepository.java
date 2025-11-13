package com.transportes.transporte.repository;

import com.transportes.transporte.entities.Tramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TramoRepository extends JpaRepository<Tramo, Long> {
    
    // Este será muy útil para obtener todos los tramos de una solicitud específica
    List<Tramo> findBySolicitudId(Long solicitudId);
    
    // Y este para encontrar tramos por su estado
    List<Tramo> findByEstado(String estado);
    
    // O para encontrar el tramo activo de un camión
    // Optional<Tramo> findByCamionIdAndEstado(Long camionId, String estado);
}