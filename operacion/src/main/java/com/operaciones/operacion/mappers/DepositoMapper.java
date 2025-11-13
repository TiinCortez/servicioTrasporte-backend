package com.operaciones.operacion.mappers;

import com.operaciones.operacion.dto.DepositoDTO;
import com.operaciones.operacion.entities.Deposito;
import org.springframework.stereotype.Component;

@Component // <-- ¡Importante! Esto hace que Spring lo gestione
public class DepositoMapper {

    // ¡Este es el método nuevo que evita la repetición!
    // (Ahora es 'public' para que el Service pueda verlo)
    public DepositoDTO convertiEntidadADTO(Deposito entidad) {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDireccion(entidad.getDireccion());
        dto.setLat(entidad.getLat());
        dto.setLng(entidad.getLng());
        dto.setCostoEstadiaDiario(entidad.getCostoEstadiaDiario());
        dto.setCreadoEn(entidad.getCreadoEn());
        return dto;
    }

    // Mapea DTO a entidad para persistir
    // (Ahora es 'public' para que el Service pueda verlo)
    public Deposito convertiDTOAEntidad(DepositoDTO dto) {
        return Deposito.builder()
                .nombre(dto.getNombre())
                .direccion(dto.getDireccion())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .costoEstadiaDiario(dto.getCostoEstadiaDiario())
                .build();
    }
}