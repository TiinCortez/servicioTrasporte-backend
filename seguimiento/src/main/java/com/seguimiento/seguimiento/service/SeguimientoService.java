package com.seguimiento.seguimiento.service;

import com.seguimiento.seguimiento.dto.EventoSeguimientoRequestDTO;
import com.seguimiento.seguimiento.dto.EventoSeguimientoResponseDTO;
import com.seguimiento.seguimiento.dto.ResumenSeguimientoDTO;
import com.seguimiento.seguimiento.entities.EventoSeguimiento;
import com.seguimiento.seguimiento.mappers.EventoSeguimientoMapper;
import com.seguimiento.seguimiento.repository.EventoSeguimientoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeguimientoService {

    private final EventoSeguimientoRepository repository;
    private final EventoSeguimientoMapper mapper;

    // ya lo tenías
    public List<EventoSeguimientoResponseDTO> obtenerLineaTiempo(Long solicitudId) {
        List<EventoSeguimiento> eventos =
                repository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);
        return eventos.stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ya lo tenías
    public EventoSeguimientoResponseDTO registrarEvento(Long solicitudId,
                                                        EventoSeguimientoRequestDTO request) {
        EventoSeguimiento entity = mapper.toEntity(solicitudId, request);
        EventoSeguimiento guardado = repository.save(entity);
        return mapper.toDTO(guardado);
    }

    // 🔹 NUEVO: resumen de seguimiento
    public ResumenSeguimientoDTO obtenerResumen(Long solicitudId) {
        List<EventoSeguimiento> eventos =
                repository.findBySolicitudIdOrderByFechaHoraAsc(solicitudId);

        if (eventos.isEmpty()) {
            return null; // el controller después decide si devuelve 404 o algo
        }

        EventoSeguimiento primero = eventos.get(0);
        EventoSeguimiento ultimo = eventos.get(eventos.size() - 1);

        ResumenSeguimientoDTO resumen = new ResumenSeguimientoDTO();
        resumen.setSolicitudId(solicitudId);
        resumen.setFechaHoraInicio(primero.getFechaHora());
        resumen.setFechaHoraFin(ultimo.getFechaHora());
        resumen.setEstadoActual(ultimo.getTipo());
        resumen.setTotalEventos(eventos.size());

        return resumen;
    }
}
