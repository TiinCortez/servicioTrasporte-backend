package com.tarifas.tarifa.service;

import com.tarifas.tarifa.dto.CalculoRequestDTO;
import com.tarifas.tarifa.dto.CalculoResponseDTO;
import com.tarifas.tarifa.dto.TarifaBaseDTO;
import com.tarifas.tarifa.entities.Recargo;
import com.tarifas.tarifa.entities.TarifaBase;
import com.tarifas.tarifa.repository.RecargoRepository;
import com.tarifas.tarifa.repository.TarifaBaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TarifaService {

    @Autowired
    private TarifaBaseRepository tarifaBaseRepository;

    @Autowired
    private RecargoRepository recargoRepository;

    @Transactional(readOnly = true)
    public CalculoResponseDTO calcularCosto(CalculoRequestDTO request) {
        
        // 1. Buscar la tarifa base (Por ahora, buscamos la primera que exista)
        TarifaBase tarifa = tarifaBaseRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException(
                    "No se encontró una tarifa base con ID 1. " +
                    "¡Asegúrate de insertar datos de prueba en la BD!"
                ));

        // 2. Buscar TODOS los recargos
        List<Recargo> recargos = recargoRepository.findAll();

        // 3. Calcular el costo base
        // Costo = (Precio Fijo) + (Distancia * Precio por KM)
        BigDecimal costo = tarifa.getPrecioBase()
            .add(request.getDistanciaKm().multiply(tarifa.getPrecioPorKm()));

        // 4. Aplicar Recargos
        for (Recargo recargo : recargos) {
            if ("FIJO".equalsIgnoreCase(recargo.getTipo())) {
                costo = costo.add(recargo.getValor());
            } else if ("PORCENTAJE".equalsIgnoreCase(recargo.getTipo())) {
                BigDecimal montoRecargo = costo.multiply(recargo.getValor().divide(new BigDecimal("100")));
                costo = costo.add(montoRecargo);
            }
        }
        
        System.out.println("Costo final calculado por ms-tarifa: $" + costo);
        return new CalculoResponseDTO(costo);
    }

    /**
     * Endpoint de gestión para crear una nueva Tarifa Base
     */
    @Transactional
    public TarifaBaseDTO createTarifaBase(TarifaBaseDTO dto) {
        TarifaBase tarifa = TarifaBase.builder()
                .nombre(dto.getNombre())
                .precioBase(dto.getPrecioBase())
                .precioPorKm(dto.getPrecioPorKm())
                .build();
        
        tarifa = tarifaBaseRepository.save(tarifa);
        dto.setId(tarifa.getId());
        return dto;
    }
    
    // (Aquí irían los otros métodos CRUD para Recargo, etc.)
}