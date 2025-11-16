package com.seguimiento.seguimiento.controllers;

import com.seguimiento.seguimiento.dto.EventoSeguimientoRequestDTO;
import com.seguimiento.seguimiento.dto.EventoSeguimientoResponseDTO;
import com.seguimiento.seguimiento.dto.ResumenSeguimientoDTO;
import com.seguimiento.seguimiento.service.SeguimientoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SeguimientoControllerTest {

    @Test
    void getLineaTiempo_returnsOk() throws Exception {
        EventoSeguimientoResponseDTO e = new EventoSeguimientoResponseDTO();
        e.setId(1L);
        e.setTipo("CREACION");

        SeguimientoService mockService = Mockito.mock(SeguimientoService.class);
        when(mockService.obtenerLineaTiempo(10L)).thenReturn(List.of(e));

        SeguimientoController controller = new SeguimientoController(mockService);

        var response = controller.getLineaTiempo(10L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(1L, response.getBody().get(0).getId());
    }

    @Test
    void registrarEvento_returnsCreated() throws Exception {
        EventoSeguimientoRequestDTO req = new EventoSeguimientoRequestDTO();
        req.setTipo("TRASLADO");

        EventoSeguimientoResponseDTO created = new EventoSeguimientoResponseDTO();
        created.setId(5L);
        created.setTipo("TRASLADO");

        SeguimientoService mockService = Mockito.mock(SeguimientoService.class);
        when(mockService.registrarEvento(any(Long.class), any(EventoSeguimientoRequestDTO.class))).thenReturn(created);

        SeguimientoController controller = new SeguimientoController(mockService);

        // setup request context for location
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/tracking/solicitudes/1/eventos");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = controller.registrarEvento(1L, req);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(5L, response.getBody().getId());
    }

    @Test
    void getResumen_returnsNotFound_andOk() throws Exception {
        SeguimientoService mockService = Mockito.mock(SeguimientoService.class);
        when(mockService.obtenerResumen(20L)).thenReturn(null);

        SeguimientoController controller = new SeguimientoController(mockService);
        var respNotFound = controller.getResumen(20L);
        assertEquals(404, respNotFound.getStatusCodeValue());

        ResumenSeguimientoDTO resumen = new ResumenSeguimientoDTO();
        // no fields required for this test
        when(mockService.obtenerResumen(21L)).thenReturn(resumen);
        var respOk = controller.getResumen(21L);
        assertEquals(200, respOk.getStatusCodeValue());
    }
}

