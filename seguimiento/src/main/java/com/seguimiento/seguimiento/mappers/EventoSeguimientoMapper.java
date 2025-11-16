package com.seguimiento.seguimiento.mappers;

import com.seguimiento.seguimiento.dto.EventoSeguimientoRequestDTO;
import com.seguimiento.seguimiento.dto.EventoSeguimientoResponseDTO;
import com.seguimiento.seguimiento.entities.EventoSeguimiento;
import org.springframework.stereotype.Component;

@Component
public class EventoSeguimientoMapper {

    public EventoSeguimientoResponseDTO toDTO(EventoSeguimiento entity) {
        EventoSeguimientoResponseDTO dto = new EventoSeguimientoResponseDTO();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setSolicitudId(entity.getSolicitudId());
        dto.setTramoId(entity.getTramoId());
        dto.setFechaHora(entity.getFechaHora());
        dto.setDescripcion(entity.getDescripcion());
        dto.setLat(entity.getLat());
        dto.setLng(entity.getLng());
        return dto;
    }

    public EventoSeguimiento toEntity(Long solicitudId, EventoSeguimientoRequestDTO dto) {
        return EventoSeguimiento.builder()
                .tipo(dto.getTipo())
                .solicitudId(solicitudId)
                .tramoId(dto.getTramoId())
                .descripcion(dto.getDescripcion())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();
    }
}
