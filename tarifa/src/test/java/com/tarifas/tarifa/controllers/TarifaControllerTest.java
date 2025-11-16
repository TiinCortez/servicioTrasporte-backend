package com.tarifas.tarifa.controllers;

import com.tarifas.tarifa.dto.CalculoRequestDTO;
import com.tarifas.tarifa.dto.CalculoResponseDTO;
import com.tarifas.tarifa.dto.TarifaBaseDTO;
import com.tarifas.tarifa.service.TarifaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TarifaControllerTest {

    @Test
    void calcular_returnsOk() throws Exception {
        CalculoRequestDTO req = new CalculoRequestDTO();
        req.setDistanciaKm(BigDecimal.valueOf(10));

        CalculoResponseDTO resp = new CalculoResponseDTO(BigDecimal.valueOf(123.45));

        TarifaService mockService = Mockito.mock(TarifaService.class);
        when(mockService.calcularCosto(any(CalculoRequestDTO.class))).thenReturn(resp);

        TarifaController controller = new TarifaController();
        Field f = TarifaController.class.getDeclaredField("tarifaService");
        f.setAccessible(true);
        f.set(controller, mockService);

        var response = controller.calcular(req);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new BigDecimal("123.45"), response.getBody().getCostoEstimado());
    }

    @Test
    void crearTarifaBase_returnsCreated() throws Exception {
        TarifaBaseDTO dto = new TarifaBaseDTO();
        dto.setNombre("Base A");
        dto.setPrecioBase(BigDecimal.valueOf(100));
        dto.setPrecioPorKm(BigDecimal.valueOf(10));

        TarifaBaseDTO created = new TarifaBaseDTO();
        created.setId(7L);
        created.setNombre("Base A");

        TarifaService mockService = Mockito.mock(TarifaService.class);
        when(mockService.createTarifaBase(any(TarifaBaseDTO.class))).thenReturn(created);

        TarifaController controller = new TarifaController();
        Field f = TarifaController.class.getDeclaredField("tarifaService");
        f.setAccessible(true);
        f.set(controller, mockService);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/tarifas/base");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = controller.crearTarifaBase(dto);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(7L, response.getBody().getId());
    }
}

