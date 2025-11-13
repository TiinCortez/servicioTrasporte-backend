package com.tarifas.tarifa.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal; 

@Entity
@Table(name="recargo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Recargo {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;
    
    @Column(name = "tipo", nullable = false, length = 12)
    private String tipo;  // FIJO | PORCENTAJE
    
    @Column(name = "valor", nullable = false, precision = 14, scale = 4)
    private BigDecimal valor; 
}