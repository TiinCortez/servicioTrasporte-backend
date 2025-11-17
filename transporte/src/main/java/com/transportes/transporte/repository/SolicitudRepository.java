package com.transportes.transporte.repository;

import com.transportes.transporte.entities.Solicitud;
import jakarta.persistence.LockModeType; // <-- ¡AÑADE ESTE IMPORT!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock; // <-- ¡AÑADE ESTE IMPORT!
import org.springframework.data.jpa.repository.Query; // <-- ¡AÑADE ESTE IMPORT!
import org.springframework.data.repository.query.Param; // <-- ¡AÑADE ESTE IMPORT!
import org.springframework.stereotype.Repository;

import java.util.Optional; // <-- ¡AÑADE ESTE IMPORT!
// ... (tus otros imports)

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {
    
    // ... (tus otros métodos como findByClienteId, etc.) ...
    
    // --- ¡AÑADE ESTE MÉTODO COMPLETO! ---
    /**
     * Busca el 'numeroSolicitud' más alto para un año específico (ej: "2025-%").
     * Usa un PESSIMISTIC_WRITE lock para evitar "race conditions", asegurando que
     * dos solicitudes creadas al mismo tiempo no obtengan el mismo número.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(s.numeroSolicitud) FROM Solicitud s WHERE s.numeroSolicitud LIKE :yearPrefix")
    Optional<String> findMaxNumeroSolicitudByYear(@Param("yearPrefix") String yearPrefix);

    // Buscar solicitud por su número (ej: "2025-AB12CD34")
    Optional<Solicitud> findByNumeroSolicitud(String numeroSolicitud);
}