package com.operaciones.operacion.dto;

import lombok.Data;

@Data
public class ClienteDTO {
    private Long id;
    private String razonSocial;
    private String cuit;
    private String direccion;
    private String telefono;
    private String email;
}