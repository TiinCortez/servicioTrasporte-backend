// Ubicación: transporte/src/main/java/com/transportes/transporte/service/SolicitudService.java
package com.transportes.transporte.service;

import com.transportes.transporte.dto.*;
import com.transportes.transporte.entities.Solicitud;
import com.transportes.transporte.entities.Tramo;
import com.transportes.transporte.mappers.SolicitudMapper; 
import com.transportes.transporte.repository.SolicitudRepository;
import com.transportes.transporte.repository.TramoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor; 
import lombok.Data; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.Year;
import java.util.*;
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

    @Autowired
    private SolicitudMapper mapper; 


    private String OPENCAGE_API_KEY ="d87d4d3b0db84826a9ffcee7f1e2d00a";

    private List<DepositoDTO> depositCache = null;
    private final Map<String, OpenCageGeometry> geocodingCache = new ConcurrentHashMap<>();
    private long lastNominatimCall = 0; // (Lo mantenemos por si OpenCage también tiene límites)
    private static final long MIN_DELAY_MS = 1100;

    /**
     * Llama a OpenCage para geocodificar.
     */
    private synchronized OpenCageGeometry getCoordinates(String address) {
        String cacheKey = address.toLowerCase().trim();
        if (geocodingCache.containsKey(cacheKey)) {
            System.out.println("Usando coordenadas desde cache para: " + address);
            return geocodingCache.get(cacheKey);
        }

        // ... (Tu lógica de rate limit está perfecta) ...
        long elapsed = System.currentTimeMillis() - lastNominatimCall;
        if (elapsed < MIN_DELAY_MS) {
            try {
                long waitTime = MIN_DELAY_MS - elapsed;
                System.out.println("Esperando " + waitTime + "ms para respetar rate limit de Nominatim...");
                Thread.sleep(waitTime);
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        String opencageUrl = UriComponentsBuilder
                .fromUriString("https://api.opencagedata.com/geocode/v1/json")
                .queryParam("q", address)
                .queryParam("key", OPENCAGE_API_KEY)
                .queryParam("countrycode", "ar")
                .queryParam("limit", 1)
                .toUriString();
        System.out.println("URL OpenCage: " + opencageUrl);

        try {
            OpenCageResponse response = restTemplate.getForObject(opencageUrl, OpenCageResponse.class);

            // --- ¡¡ARREGLO #3: CHEQUEO DE NULL MÁS SEGURO!! ---
            if (response == null || response.getResults() == null || response.getResults().isEmpty() || response.getResults().get(0).getGeometry() == null) {
                throw new RuntimeException(
                        "No se encontraron coordenadas para la direccion: '" + address + "'. "
                );
            }

            OpenCageGeometry result = response.getResults().get(0).getGeometry();
            geocodingCache.put(cacheKey, result);
            System.out.println("Coordenadas encontradas y guardadas en cache: " + result.getLat() + ", " + result.getLng());
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
    
    /**
     * Llama a OSRM y ms-tarifa para calcular los datos de UN solo tramo (una ruta).
     */
    private RutaCalculada calcularRutaYCostos(double lonOrigen, double latOrigen, double lonDestino, double latDestino) {
        System.out.println(String.format("Calculando ruta para: %f,%f -> %f,%f", lonOrigen, latOrigen, lonDestino, latDestino));
        
        // Paso 1: Llamar a OSRM (público)
        String osrmCoordinates = String.format("%f,%f;%f,%f", lonOrigen, latOrigen, lonDestino, latDestino);
        String osrmUrl = "http://routing.openstreetmap.de/routed-car/route/v1/driving/" + osrmCoordinates + "?overview=false";
        
        Integer tiempoEstimado;
        BigDecimal distanciaKm;
        
        try {
            OsrmResponse osrmResponse = restTemplate.getForObject(osrmUrl, OsrmResponse.class);
            OsrmRoute route = osrmResponse.getRoutes().get(0);
            distanciaKm = BigDecimal.valueOf(route.getDistance() / 1000.0);
            tiempoEstimado = (int) (route.getDuration() / 60.0);
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a OSRM: " + e.getMessage(), e);
        }
        
        // Paso 2: Llamar a ms-tarifa
        String tarifaUrl = "http://ms-tarifa:8084/api/tarifas/calcular";
        CalculoRequestDTO tarifaRequest = new CalculoRequestDTO();
        tarifaRequest.setDistanciaKm(distanciaKm);
        BigDecimal costoEstimado;
        
        try {
            CalculoResponseDTO tarifaResponse = restTemplate.postForObject(tarifaUrl, tarifaRequest, CalculoResponseDTO.class);
            costoEstimado = tarifaResponse.getCostoEstimado();
        } catch (Exception e) {
            throw new RuntimeException("Error al llamar a ms-tarifa: " + e.getMessage(), e);
        }
        
        System.out.println(String.format("Tramo calculado: %.2f km, %d min, $%.2f", distanciaKm, tiempoEstimado, costoEstimado));
        return new RutaCalculada(distanciaKm, tiempoEstimado, costoEstimado);
    }


    // --- ¡MÉTODO PRINCIPAL CON 3 TRAMOS! ---
    @Transactional
    public SolicitudResponseDTO createSolicitud(SolicitudRequestDTO requestDTO) {

        System.out.println("Iniciando creacion de solicitud...");
        System.out.println("Origen: " + requestDTO.getOrigenDir());
        System.out.println("Destino: " + requestDTO.getDestinoDir());

        // Paso 1: Geocodificacion y Depósitos
        OpenCageGeometry coordsOrigen = getCoordinates(requestDTO.getOrigenDir());
        OpenCageGeometry coordsDestino = getCoordinates(requestDTO.getDestinoDir());
        List<DepositoDTO> allDeposits = getDepositos();
        DepositoDTO depOrigen = findClosestDeposit(allDeposits, coordsOrigen.getLat(), coordsOrigen.getLng());
        DepositoDTO depDestino = findClosestDeposit(allDeposits, coordsDestino.getLat(), coordsDestino.getLng());

        System.out.println("Depósito Origen más cercano: " + depOrigen.getNombre());
        System.out.println("Depósito Destino más cercano: " + depDestino.getNombre());
        
        // Paso 2: Cálculo de los 3 Tramos (Viajes)
        RutaCalculada tramo1 = calcularRutaYCostos(
            coordsOrigen.getLng(), coordsOrigen.getLat(), 
            depOrigen.getLng(), depOrigen.getLat()
        );
        RutaCalculada tramo2 = calcularRutaYCostos(
            depOrigen.getLng(), depOrigen.getLat(), 
            depDestino.getLng(), depDestino.getLat()
        );
        RutaCalculada tramo3 = calcularRutaYCostos(
            depDestino.getLng(), depDestino.getLat(), 
            coordsDestino.getLng(), coordsDestino.getLat()
        );

        // Paso 3: Cálculo de Totales (Viajes + Estadía)
        BigDecimal costoTotalViajes = tramo1.getCostoEstimado()
                                      .add(tramo2.getCostoEstimado())
                                      .add(tramo3.getCostoEstimado());
        BigDecimal costoEstadiaOrigen = (depOrigen.getCostoEstadiaDiario() != null) ? depOrigen.getCostoEstadiaDiario() : BigDecimal.ZERO;
        BigDecimal costoEstadiaDestino = (depDestino.getCostoEstadiaDiario() != null) ? depDestino.getCostoEstadiaDiario() : BigDecimal.ZERO;
        BigDecimal costoTotalEstadia = costoEstadiaOrigen.add(costoEstadiaDestino);
        BigDecimal costoTotal = costoTotalViajes.add(costoTotalEstadia);
        Integer tiempoTotal = tramo1.getTiempoEstimadoMin() + tramo2.getTiempoEstimadoMin() + tramo3.getTiempoEstimadoMin();

        System.out.println("Costos de Viaje: $" + costoTotalViajes);
        System.out.println("Costos de Estadía (Depósitos): $" + costoTotalEstadia);
        System.out.println("COSTO TOTAL ESTIMADO: $" + costoTotal);

        // Paso 4: Reserva de Contenedor (Llamada a ms-operaciones)
        String url = "http://ms-operaciones:8082/api/operaciones/contenedores/" 
                       + requestDTO.getContenedorId() 
                       + "/estado";
        ActualizacionEstadoRequestDTO estadoRequest = new ActualizacionEstadoRequestDTO("EN PREPARACION");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ActualizacionEstadoRequestDTO> entity = new HttpEntity<>(estadoRequest, headers);
        try {
            System.out.println("Actualizando estado del contenedor " + requestDTO.getContenedorId());
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            System.out.println("Contenedor actualizado a EN PREPARACION");
        } catch (HttpClientErrorException ex) {
            throw new RuntimeException("Error al reservar el contenedor: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Error al reservar el contenedor: " + e.toString(), e);
        }

        // Paso 5: Generar numero de solicitud
        String numeroSolicitudGenerado = Year.now().getValue() + "-" + 
                                         UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Paso 6: Guardar la Solicitud (Padre)
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
                .tiempoEstimadoMin(tiempoTotal)
                .costoEstimado(costoTotal)
                .build();
        Solicitud solicitudGuardada = solicitudRepository.save(nuevaSolicitud);

        // Paso 7: Guardar los Tramos (Hijos)
        List<Tramo> tramosAGuardar = new ArrayList<>();
        tramosAGuardar.add(Tramo.builder()
            .solicitud(solicitudGuardada).estado("PENDIENTE")
            .origenDir(requestDTO.getOrigenDir()).destinoDir(depOrigen.getNombre())
            .depositoOrigenId(null).depositoDestinoId(depOrigen.getId())
            .nombreDepositoOrigen(null).nombreDepositoDestino(depOrigen.getNombre())
            .distanciaKm(tramo1.getDistanciaKm()).duracionMin(tramo1.getTiempoEstimadoMin())
            .costoRealTramo(tramo1.getCostoEstimado())
            .build());
        tramosAGuardar.add(Tramo.builder()
            .solicitud(solicitudGuardada).estado("PENDIENTE")
            .origenDir(depOrigen.getNombre()).destinoDir(depDestino.getNombre())
            .depositoOrigenId(depOrigen.getId()).depositoDestinoId(depDestino.getId())
            .nombreDepositoOrigen(depOrigen.getNombre()).nombreDepositoDestino(depDestino.getNombre())
            .distanciaKm(tramo2.getDistanciaKm()).duracionMin(tramo2.getTiempoEstimadoMin())
            .costoRealTramo(tramo2.getCostoEstimado())
            .build());
        tramosAGuardar.add(Tramo.builder()
            .solicitud(solicitudGuardada).estado("PENDIENTE")
            .origenDir(depDestino.getNombre()).destinoDir(requestDTO.getDestinoDir())
            .depositoOrigenId(depDestino.getId()).depositoDestinoId(null)
            .nombreDepositoOrigen(depDestino.getNombre()).nombreDepositoDestino(null)
            .distanciaKm(tramo3.getDistanciaKm()).duracionMin(tramo3.getTiempoEstimadoMin())
            .costoRealTramo(tramo3.getCostoEstimado())
            .build());
        tramoRepository.saveAll(tramosAGuardar);

        System.out.println("Solicitud creada exitosamente: " + numeroSolicitudGenerado);

        // Paso 8: Devolver respuesta (¡USANDO EL MAPPER!)
        return mapper.mapEntidadToDto(solicitudGuardada);
    }
    
    @Transactional(readOnly = true)
    public SolicitudResponseDTO getSolicitudById(Long id) {
        Solicitud solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada"));
        return mapper.mapEntidadToDto(solicitud);
    }
    
    @Transactional(readOnly = true)
    public List<SolicitudResponseDTO> getAllSolicitudes() {
        return solicitudRepository.findAll().stream()
                .map(mapper::mapEntidadToDto)
                .collect(Collectors.toList());
    }
    // --- ¡¡ARREGLO #2: CLASE AUXILIAR INTERNA!! ---
    // (Pon esta clase pequeña al final de tu archivo SolicitudService.java)
    @Data
    @AllArgsConstructor
    private static class RutaCalculada {
        private BigDecimal distanciaKm;
        private Integer tiempoEstimadoMin;
        private BigDecimal costoEstimado;
    }


    @Transactional
    public Tramo asignarCamionATramo(Long tramoId, Long camionId) {

        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No existe tramo con id " + tramoId));

        tramo.setCamionId(camionId);
        tramo.setEstado("ASIGNADO");

        return tramoRepository.save(tramo);
    }

    @Transactional
    public List<Tramo> asignarCamionASolicitud(Long solicitudId, Long camionId) {

        // 1) Validar que exista la solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "No existe solicitud con id " + solicitudId
                ));

        // 2) Obtener los tramos de esa solicitud
        List<Tramo> tramos = tramoRepository.findBySolicitudId(solicitud.getId());

        if (tramos.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud " + solicitudId + " no tiene tramos para asignar"
            );
        }

        // 3) Obtener datos del camión y del contenedor desde ms-operaciones
        CamionDTO camion = obtenerCamion(camionId);
        ContenedorDTO contenedor = obtenerContenedor(solicitud.getContenedorId());

        // 4) Validar capacidad (peso + volumen simbólico) + disponibilidad
        validarCapacidad(camion, contenedor);

        // 5) Asignar el camión a TODOS los tramos
        for (Tramo tramo : tramos) {
            tramo.setCamionId(camionId);
            tramo.setEstado("ASIGNADO");
        }

        // 6) Guardar cambios y devolverlos
        return tramoRepository.saveAll(tramos);
    }

    // ----------------- HELPERS EXTERNOS A MS-OPERACIONES -----------------

    private CamionDTO obtenerCamion(Long camionId) {
        String url = "http://ms-operaciones:8082/api/operaciones/camiones/" + camionId;
        CamionDTO camion = restTemplate.getForObject(url, CamionDTO.class);

        if (camion == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se encontró el camión con id " + camionId + " en ms-operaciones"
            );
        }
        return camion;
    }

    private ContenedorDTO obtenerContenedor(Long contenedorId) {
        String url = "http://ms-operaciones:8082/api/operaciones/contenedores/" + contenedorId;
        ContenedorDTO contenedor = restTemplate.getForObject(url, ContenedorDTO.class);

        if (contenedor == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se encontró el contenedor con id " + contenedorId + " en ms-operaciones"
            );
        }
        return contenedor;
    }

    private void validarCapacidad(CamionDTO camion, ContenedorDTO contenedor) {

        // 1) Disponibilidad del camión
        if (!Boolean.TRUE.equals(camion.getDisponible())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El camión no está disponible"
            );
        }

        // 2) Validación REAL de peso
        if (camion.getCapPesoKg() == null) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "El camión no tiene configurada la capacidad de peso"
            );
        }

        if (contenedor.getCapacidadKg() != null &&
                BigDecimal.valueOf(contenedor.getCapacidadKg())
                        .compareTo(camion.getCapPesoKg()) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El contenedor excede la capacidad de peso del camión ("
                            + camion.getCapPesoKg() + " kg)"
            );
        }

        // 3) Validación simple de volumen (asumimos 10 m3 para el contenedor)
        if (camion.getCapVolM3() != null) {
            BigDecimal volumenContenedor = new BigDecimal("10"); // valor fijo para el TP

            if (camion.getCapVolM3().compareTo(volumenContenedor) < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El contenedor no entra en el camión por volumen (se asume 10 m3)"
                );
            }
        }
    }

    @Transactional
    public Tramo iniciarTramo(Long tramoId) {

        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe tramo " + tramoId));

        if (!"ASIGNADO".equals(tramo.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tramo debe estar ASIGNADO para poder iniciarse");
        }

        tramo.setEstado("EN_CURSO");
        tramo.setFechaHoraInicio(OffsetDateTime.now());

        tramoRepository.save(tramo);

        enviarEventoSeguimiento("INICIO_TRAMO", tramo);

        return tramo;
    }

    @Transactional
    public Tramo finalizarTramo(Long tramoId) {

        Tramo tramo = tramoRepository.findById(tramoId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe tramo " + tramoId));

        if (!"EN_CURSO".equals(tramo.getEstado())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El tramo debe estar EN_CURSO para finalizarse");
        }

        tramo.setEstado("FINALIZADO");
        tramo.setFechaHoraFin(OffsetDateTime.now());

        tramoRepository.save(tramo);

        enviarEventoSeguimiento("FIN_TRAMO", tramo);

        return tramo;
    }


    @Transactional
    public SolicitudResponseDTO finalizarSolicitud(Long solicitudId) {

        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La solicitud no existe"));

        // Obtener tramos desde BD
        List<Tramo> tramos = tramoRepository.findBySolicitudId(solicitudId);

        // Validar estado
        boolean todosFinalizados = tramos.stream()
                .allMatch(t -> "FINALIZADO".equals(t.getEstado()));

        if (!todosFinalizados) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud aún tiene tramos sin finalizar");
        }

        // ---------- CALCULAR COSTO TOTAL ----------
        BigDecimal costoTotal = tramos.stream()
                .map(Tramo::getCostoRealTramo)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        solicitud.setCostoFinal(costoTotal);

        // ---------- CALCULAR TIEMPO REAL ----------
        long tiempoTotalMin = tramos.stream()
                .filter(t -> t.getFechaHoraInicio() != null && t.getFechaHoraFin() != null)
                .mapToLong(t -> Duration.between(t.getFechaHoraInicio(), t.getFechaHoraFin()).toMinutes())
                .sum();

        solicitud.setTiempoRealMin((int) tiempoTotalMin);

        // ---------- CAMBIAR ESTADO ----------
        solicitud.setEstado("COMPLETADA");

        solicitudRepository.save(solicitud);

        // ---------- REGISTRO A SEGUIMIENTO ----------
        enviarEventoSeguimientoSolicitud("ENTREGADO", solicitud);

        return mapper.mapEntidadToDto(solicitud);
    }


    private void enviarEventoSeguimiento(String tipo, Tramo tramo) {

        try {
            String url = "http://ms-seguimiento:8085/api/tracking/eventos";

            Map<String, Object> body = Map.of(
                    "tipo", tipo,
                    "solicitudId", tramo.getSolicitud().getId(),
                    "tramoId", tramo.getId(),
                    "descripcion", tipo + " del tramo " + tramo.getId()
            );

            restTemplate.postForObject(url, body, Void.class);
        }
        catch (Exception e) {
            System.out.println("⚠ No se pudo registrar evento en seguimiento: " + e.getMessage());
        }
    }

    private void enviarEventoSeguimientoSolicitud(String tipo, Solicitud solicitud) {
        try {
            String url = "http://ms-seguimiento:8085/api/tracking/eventos";

            Map<String, Object> body = Map.of(
                    "tipo", tipo,
                    "solicitudId", solicitud.getId(),
                    "descripcion", tipo + " de la solicitud " + solicitud.getId()
            );

            restTemplate.postForObject(url, body, Void.class);
        }
        catch (Exception e) {
            System.out.println("⚠ No se pudo registrar evento en seguimiento: " + e.getMessage());
        }
    }




}

