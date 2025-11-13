package com.operaciones.operacion.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;


@Data
public class DepositoDTO {
    //Aca definimos que vamos a leer del deposito con la URL /depositos o /depositos/{id}
    private Long id;
    private String nombre;
    private String direccion;
    private Double lat;
    private Double lng;
    private BigDecimal costoEstadiaDiario;
    private OffsetDateTime creadoEn;
}

