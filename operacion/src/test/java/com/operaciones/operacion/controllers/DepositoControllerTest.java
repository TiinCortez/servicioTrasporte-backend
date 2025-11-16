package com.operaciones.operacion.controllers;

import com.operaciones.operacion.dto.DepositoDTO;
import com.operaciones.operacion.service.DepositoService;
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

public class DepositoControllerTest {

    @Test
    void getById_returnsOk() throws Exception {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(1L);
        dto.setNombre("Dep A");
        dto.setLat( -34.0 );
        dto.setLng( -58.0 );
        dto.setCostoEstadiaDiario(BigDecimal.ZERO);

        DepositoService mockService = Mockito.mock(DepositoService.class);
        when(mockService.getDepositoById(1L)).thenReturn(dto);

        DepositoController controller = new DepositoController();
        Field f = DepositoController.class.getDeclaredField("depositoService");
        f.setAccessible(true);
        f.set(controller, mockService);

        var response = controller.getById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAll_returnsList() throws Exception {
        DepositoDTO dto = new DepositoDTO();
        dto.setId(1L);
        dto.setNombre("Dep A");

        DepositoService mockService = Mockito.mock(DepositoService.class);
        when(mockService.getAllDepositos()).thenReturn(List.of(dto));

        DepositoController controller = new DepositoController();
        Field f = DepositoController.class.getDeclaredField("depositoService");
        f.setAccessible(true);
        f.set(controller, mockService);

        var response = controller.getAll();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void createDeposito_returnsCreated() throws Exception {
        DepositoDTO toCreate = new DepositoDTO();
        toCreate.setNombre("New Dep");

        DepositoDTO created = new DepositoDTO();
        created.setId(5L);
        created.setNombre("New Dep");

        DepositoService mockService = Mockito.mock(DepositoService.class);
        when(mockService.createDeposito(any(DepositoDTO.class))).thenReturn(created);

        DepositoController controller = new DepositoController();
        Field f = DepositoController.class.getDeclaredField("depositoService");
        f.setAccessible(true);
        f.set(controller, mockService);

        // setup request context so ServletUriComponentsBuilder can build Location
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/operaciones/depositos");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var response = controller.createDeposito(toCreate);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals(5L, response.getBody().getId());
    }
}

