package com.transportes.transporte.controllers;

import com.transportes.transporte.dto.SolicitudRequestDTO;
import com.transportes.transporte.dto.SolicitudResponseDTO;
import com.transportes.transporte.service.SolicitudService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SolicitudControllerTest {

    @Test
    void createSolicitud_returnsCreatedAndBody_withoutSpringContext() throws Exception {
        SolicitudRequestDTO req = new SolicitudRequestDTO();
        req.setClienteId(1L);
        req.setContenedorId(2L);
        req.setOrigenDir("Origen prueba");
        req.setDestinoDir("Destino prueba");

        SolicitudResponseDTO resp = new SolicitudResponseDTO();
        resp.setId(1L);
        resp.setNumeroSolicitud("2025-ABC12345");
        resp.setClienteId(1L);
        resp.setContenedorId(2L);
        resp.setOrigenDir("Origen prueba");
        resp.setDestinoDir("Destino prueba");
        resp.setCostoEstimado(BigDecimal.ZERO);

        SolicitudService mockService = Mockito.mock(SolicitudService.class);
        when(mockService.createSolicitud(any(SolicitudRequestDTO.class))).thenReturn(resp);

        SolicitudController controller = new SolicitudController();
        // Inyectar el mock por reflexión
        Field f = SolicitudController.class.getDeclaredField("solicitudService");
        f.setAccessible(true);
        f.set(controller, mockService);

        // Preparar RequestContext para que ServletUriComponentsBuilder funcione
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/transporte/solicitudes");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = controller.create(req);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAllSolicitudes_returnsList_withoutSpringContext() throws Exception {
        SolicitudResponseDTO resp = new SolicitudResponseDTO();
        resp.setId(1L);
        resp.setNumeroSolicitud("2025-ABC12345");

        SolicitudService mockService = Mockito.mock(SolicitudService.class);
        when(mockService.getAllSolicitudes()).thenReturn(List.of(resp));

        SolicitudController controller = new SolicitudController();
        Field f = SolicitudController.class.getDeclaredField("solicitudService");
        f.setAccessible(true);
        f.set(controller, mockService);

        var response = controller.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }
}
