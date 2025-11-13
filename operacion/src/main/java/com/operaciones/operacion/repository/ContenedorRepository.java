package com.operaciones.operacion.repository;

import com.operaciones.operacion.entities.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
	// Buscar contenedores por cliente
	java.util.List<Contenedor> findByClienteId(Long clienteId);
}
