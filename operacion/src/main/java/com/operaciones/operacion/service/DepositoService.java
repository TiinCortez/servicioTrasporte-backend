package com.operaciones.operacion.service;

import com.operaciones.operacion.dto.DepositoDTO;
import com.operaciones.operacion.entities.Deposito;
import com.operaciones.operacion.mappers.DepositoMapper; 
import com.operaciones.operacion.repository.DepositoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositoService {

    @Autowired
    private DepositoRepository depositoRepository;

    @Autowired
    private DepositoMapper mapper;

    public DepositoDTO getDepositoById(Long id) {
        Deposito deposito = depositoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Deposito no encontrado"));
        return mapper.convertiEntidadADTO(deposito);
    }

    public List<DepositoDTO> getAllDepositos() {
        return depositoRepository.findAll().stream()
                .map(mapper::convertiEntidadADTO) // 
                .collect(Collectors.toList());
    }

    public DepositoDTO createDeposito(DepositoDTO dto) {
        Deposito entidad = mapper.convertiDTOAEntidad(dto);
        Deposito saved = depositoRepository.save(entidad);
        return mapper.convertiEntidadADTO(saved);
    }

}