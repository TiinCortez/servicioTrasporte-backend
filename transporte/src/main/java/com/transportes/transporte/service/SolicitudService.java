package com.transportes.transporte.service;

import com.transportes.transporte.dto.*;
import com.transportes.transporte.entities.Solicitud;
import com.transportes.transporte.entities.Tramo;
import com.transportes.transporte.repository.SolicitudRepository;
import com.transportes.transporte.repository.TramoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors; 

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;
    @Autowired
    private TramoRepository tramoRepository;
    @Autowired
    private RestTemplate restTemplate;


    //ACA VA LA API KEY DE OPENCAGE
    private String OPENCAGE_API_KEY = "" ;

    private List<DepositoDTO> depositCache = null;

    private final Map<String, OpenCageGeometry> geocodingCache = new ConcurrentHashMap<>();
    // Utilizamos OpenCage en lugar de Nominatim para geocodificación ya que nos traia problemas de logs vacios.
    private synchronized OpenCageGeometry getCoordinates(String address) {
        String cacheKey = address.toLowerCase().trim();
        if (geocodingCache.containsKey(cacheKey)) {
            System.out.println("Usando coordenadas desde cache para: " + address);
            return geocodingCache.get(cacheKey);
        }

        // Construir URL de OpenCage
        String opencageUrl = UriComponentsBuilder
                .fromUriString("https://api.opencagedata.com/geocode/v1/json")
                .queryParam("q", address)
                .queryParam("key", OPENCAGE_API_KEY)
                .queryParam("countrycode", "ar") // Se filtra por direcciones de Argentina 
                .queryParam("limit", 1)
                .toUriString();

        System.out.println("URL OpenCage: " + opencageUrl);

        try {
            OpenCageResponse response = restTemplate.getForObject(opencageUrl, OpenCageResponse.class);

            if (response == null || response.getResults() == null || response.getResults().isEmpty()) {
                throw new RuntimeException(
                        "No se encontraron coordenadas para la direccion: '" + address + "'. " +
                        "Intenta ser mas especifico (ej: 'Poeta Lugones, Cordoba, Argentina')"
                );
            }

            OpenCageGeometry result = response.getResults().get(0).getGeometry();
            geocodingCache.put(cacheKey, result);
            System.out.println("Coordenadas encontradas y guardadas en cache: " + 
                               result.getLat() + ", " + result.getLng());
            return result;
            
        } catch (Exception e) {
            throw new RuntimeException("Error al geocodificar la direccion '" + address + "': " + e.getMessage(), e);
        }
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Devuelve la distancia en KM
    }
    private List<DepositoDTO> getDepositos() {
        if (depositCache != null) {
            System.out.println("Usando lista de depósitos desde cache.");
            return depositCache;
        }
        
        System.out.println("Cache de depósitos vacía. Llamando a ms-operaciones...");
        String urlDepositos = "http://ms-operaciones:8082/api/operaciones/depositos";
        try {
            DepositoDTO[] deposits = restTemplate.getForObject(urlDepositos, DepositoDTO[].class);
            if (deposits == null) {
                throw new RuntimeException("ms-operaciones devolvió una lista nula de depósitos.");
            }
            this.depositCache = Arrays.asList(deposits);
            System.out.println("Cache de depósitos actualizada. " + this.depositCache.size() + " depósitos cargados.");
            return this.depositCache;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la lista de depósitos de ms-operaciones: " + e.getMessage(), e);
        }
    }
    private DepositoDTO findClosestDeposit(List<DepositoDTO> deposits, double targetLat, double targetLon) {
        DepositoDTO closest = null;
        double minDistance = Double.MAX_VALUE;

        for (DepositoDTO deposit : deposits) {
            if (deposit.getLat() == null || deposit.getLng() == null) continue; // Ignora depósitos sin coordenadas

            double distance = haversineDistance(targetLat, targetLon, deposit.getLat(), deposit.getLng());
            if (distance < minDistance) {
                minDistance = distance;
                closest = deposit;
            }
        }
        return closest;
    }
    
// --- ¡¡MÉTODO PRINCIPAL ACTUALIZADO!! ---
    @Transactional
    public SolicitudResponseDTO createSolicitud(SolicitudRequestDTO requestDTO) {

        System.out.println("Iniciando creacion de solicitud...");
        System.out.println("Origen: " + requestDTO.getOrigenDir());
        System.out.println("Destino: " + requestDTO.getDestinoDir());

        // Paso 1: Geocodificacion (OpenCage)
        OpenCageGeometry coordsOrigen = getCoordinates(requestDTO.getOrigenDir());
        OpenCageGeometry coordsDestino = getCoordinates(requestDTO.getDestinoDir());

        // --- ¡¡NUEVO!! Paso 1.5: Encontrar depósitos más cercanos ---
        System.out.println("Buscando depósitos más cercanos...");
        List<DepositoDTO> allDeposits = getDepositos(); // Llama a 'operaciones' (o usa caché)
        
        DepositoDTO closestToOrigin = findClosestDeposit(allDeposits, coordsOrigen.getLat(), coordsOrigen.getLng());
        DepositoDTO closestToDestination = findClosestDeposit(allDeposits, coordsDestino.getLat(), coordsDestino.getLng());
        
        if (closestToOrigin == null || closestToDestination == null) {
            throw new RuntimeException("No se pudieron encontrar depósitos con coordenadas válidas.");
        }
        
        System.out.println("Depósito Origen más cercano: " + closestToOrigin.getNombre() + " (ID: " + closestToOrigin.getId() + ")");
        System.out.println("Depósito Destino más cercano: " + closestToDestination.getNombre() + " (ID: " + closestToDestination.getId() + ")");
        // --- FIN DEL NUEVO PASO ---

        // Paso 2: Ruteo (OSRM)
        String osrmCoordinates = String.format("%f,%f;%f,%f",
                coordsOrigen.getLng(), coordsOrigen.getLat(),
                coordsDestino.getLng(), coordsDestino.getLat()
        );
        String osrmUrl = "http://routing.openstreetmap.de/routed-car/route/v1/driving/" + osrmCoordinates + "?overview=false";
        
        // ... (Tu código de OSRM y cálculo de costo sigue igual)
        Integer tiempoEstimado;
        BigDecimal distanciaKm;
        try {
            OsrmResponse osrmResponse = restTemplate.getForObject(osrmUrl, OsrmResponse.class);
            OsrmRoute route = osrmResponse.getRoutes().get(0);
            distanciaKm = BigDecimal.valueOf(route.getDistance() / 1000.0);
            tiempoEstimado = (int) (route.getDuration() / 60.0);
            System.out.println("Ruta calculada: " + distanciaKm + " km, " + tiempoEstimado + " min");
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a OSRM: " + e.getMessage(), e);
        }
        
        // Paso 3: Calculo de costo (Llamada a ms-tarifa)
        String tarifaUrl = "http://ms-tarifa:8084/api/tarifas/calcular";
        CalculoRequestDTO tarifaRequest = new CalculoRequestDTO();
        tarifaRequest.setDistanciaKm(distanciaKm);
        BigDecimal costoEstimado;
        try {
            CalculoResponseDTO tarifaResponse = restTemplate.postForObject(tarifaUrl, tarifaRequest, CalculoResponseDTO.class);
            costoEstimado = tarifaResponse.getCostoEstimado();
            System.out.println("Costo estimado (real) recibido de ms-tarifa: $" + costoEstimado);
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a ms-tarifa: " + e.getMessage(), e);
        }

        // Paso 4: Llamada a ms-operaciones (Reserva de Contenedor)
        // ... (Tu código de reserva de contenedor sigue igual)
        String url = "http://ms-operaciones:8082/api/operaciones/contenedores/" 
                       + requestDTO.getContenedorId() 
                       + "/estado";
        ActualizacionEstadoRequestDTO estadoRequest = new ActualizacionEstadoRequestDTO("EN PREPARACION");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ActualizacionEstadoRequestDTO> entity = new HttpEntity<>(estadoRequest, headers);
        try {
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Error al reservar el contenedor: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Error al reservar el contenedor: " + e.toString(), e);
        }

        // Paso 5: Generar numero de solicitud
        String numeroSolicitudGenerado = Year.now().getValue() + "-" + 
                                         UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Paso 6: Crear y Guardar la 'Solicitud'
        Solicitud nuevaSolicitud = Solicitud.builder()
                .numeroSolicitud(numeroSolicitudGenerado)
                .clienteId(requestDTO.getClienteId())
                .contenedorId(requestDTO.getContenedorId())
                .origenDir(requestDTO.getOrigenDir())
                .origenLat(coordsOrigen.getLat())
                .origenLng(coordsOrigen.getLng())
                .destinoDir(requestDTO.getDestinoDir())
                .destinoLat(coordsDestino.getLat())
                .destinoLng(coordsDestino.getLng())
                .estado("PENDIENTE")
                .tiempoEstimadoMin(tiempoEstimado)
                .costoEstimado(costoEstimado)
                .build();
        Solicitud solicitudGuardada = solicitudRepository.save(nuevaSolicitud);

        // Paso 7: Crear y Guardar el Tramo 
        Tramo tramoInicial = Tramo.builder()
                .solicitud(solicitudGuardada)
                .estado("PENDIENTE")
                .origenDir(solicitudGuardada.getOrigenDir()) 
                .destinoDir(solicitudGuardada.getDestinoDir()) 
                .depositoOrigenId(closestToOrigin.getId())
                .depositoDestinoId(closestToDestination.getId()) 
                .nombreDepositoOrigen(closestToOrigin.getNombre()) 
                .nombreDepositoDestino(closestToDestination.getNombre())
                .distanciaKm(distanciaKm)
                .duracionMin(tiempoEstimado)
                .build();
        
        tramoRepository.save(tramoInicial);

        System.out.println("Solicitud creada exitosamente: " + numeroSolicitudGenerado);

        // Paso 8: Devolver respuesta
        return mapEntidadToDto(solicitudGuardada);
    }
    
    @Transactional(readOnly = true)
    public SolicitudResponseDTO getSolicitudById(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
        return mapEntidadToDto(solicitud);
    }
    
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getAllSolicitudes() {
        return solicitudRepository.findAll().stream()
                .map(this::mapEntidadToDto)
                .collect(Collectors.toList());
    }

    // Metodos de mapeo
    
    private SolicitudResponseDTO mapEntidadToDto(Solicitud entidad) {
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
                    .map(this::mapTramoToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private TramoResponseDTO mapTramoToDto(Tramo tramo) {
        TramoResponseDTO dto = new TramoResponseDTO();
        dto.setId(tramo.getId());
        if (tramo.getSolicitud() != null) {
            dto.setSolicitudId(tramo.getSolicitud().getId());
        }
        dto.setEstado(tramo.getEstado());
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