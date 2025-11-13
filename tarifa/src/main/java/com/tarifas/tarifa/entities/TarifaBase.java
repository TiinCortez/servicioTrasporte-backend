package com.tarifas.tarifa.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal; 

@Entity
@Table(name="tarifa_base")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder 
public class TarifaBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "precio_base", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioBase;  

    @Column(name = "precio_por_km", nullable = false, precision = 14, scale = 2)
    private BigDecimal precioPorKm; 
}