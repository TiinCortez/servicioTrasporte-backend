package com.operaciones.operacion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp; // Para la fecha automática
import java.time.OffsetDateTime; // Mejor que java.util.Date
import java.math.BigDecimal;

@Entity
@Table(name="deposito")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Deposito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Correcto para BIGSERIAL
    private Long id; // ¡Cambiado a Long!

    @NotBlank
    @Column(nullable=false, length=80) // Ajustado a 80, como en el SQL
    private String nombre;

    @Column(length=200) // No es @NotBlank, puede ser nulo
    private String direccion;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;
    
    private BigDecimal costoEstadiaDiario;

    @CreationTimestamp // Hace que se llene automáticamente al crear
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn; // ¡Campo añadido!
    
}