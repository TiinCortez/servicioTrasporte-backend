package com.transportes.transporte.mappers;

import com.transportes.transporte.dto.SolicitudResponseDTO;
import com.transportes.transporte.dto.TramoResponseDTO;
import com.transportes.transporte.entities.Solicitud;
import com.transportes.transporte.entities.Tramo;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component // <-- ¡Importante! Para que Spring pueda inyectarlo
public class SolicitudMapper {

    /**
     * Convierte una entidad Solicitud (de la BD) a un SolicitudResponseDTO (para la API).
     */
    public SolicitudResponseDTO mapEntidadToDto(Solicitud entidad) {
        SolicitudResponseDTO dto = new SolicitudResponseDTO();
        dto.setId(entidad.getId());
        dto.setNumeroSolicitud(entidad.getNumeroSolicitud());
        dto.setClienteId(entidad.getClienteId());
        dto.setContenedorId(entidad.getContenedorId());
        dto.setOrigenLat(entidad.getOrigenLat());
        dto.setOrigenLng(entidad.getOrigenLng());
        dto.setOrigenDir(entidad.getOrigenDir());
        dto.setDestinoLat(entidad.getDestinoLat());
        dto.setDestinoLng(entidad.getDestinoLng());
        dto.setDestinoDir(entidad.getDestinoDir());
        dto.setEstado(entidad.getEstado());
        dto.setCostoEstimado(entidad.getCostoEstimado());
        dto.setTiempoEstimadoMin(entidad.getTiempoEstimadoMin());
        dto.setCostoFinal(entidad.getCostoFinal());
        dto.setTiempoRealMin(entidad.getTiempoRealMin());
        dto.setCreadoEn(entidad.getCreadoEn());
        
        if (entidad.getTramos() != null) {
            dto.setTramos(entidad.getTramos().stream()
                    .map(this::mapTramoToDto) // ¡Llama al otro método de este mapper!
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    /**
     * Convierte una entidad Tramo (de la BD) a un TramoResponseDTO (para la API).
     */
    public TramoResponseDTO mapTramoToDto(Tramo tramo) {
        TramoResponseDTO dto = new TramoResponseDTO();
        dto.setId(tramo.getId());
        if (tramo.getSolicitud() != null) {
            dto.setSolicitudId(tramo.getSolicitud().getId());
        }
        dto.setEstado(tramo.getEstado());
        // dto.setTipo(tramo.getTipo()); // (Lo dejamos comentado por si lo borraste)
        dto.setCamionId(tramo.getCamionId());
        dto.setDepositoOrigenId(tramo.getDepositoOrigenId());
        dto.setNombreDepositoOrigen(tramo.getNombreDepositoOrigen());
        dto.setDepositoDestinoId(tramo.getDepositoDestinoId());
        dto.setNombreDepositoDestino(tramo.getNombreDepositoDestino());
        dto.setOrigenDir(tramo.getOrigenDir());
        dto.setDestinoDir(tramo.getDestinoDir());
        dto.setDistanciaKm(tramo.getDistanciaKm());
        dto.setDuracionMin(tramo.getDuracionMin());
        dto.setCostoRealTramo(tramo.getCostoRealTramo());
        dto.setFechaHoraInicio(tramo.getFechaHoraInicio());
        dto.setFechaHoraFin(tramo.getFechaHoraFin());
        return dto;
    }
}