package com.operaciones.operacion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

@Entity
@Table(name="contenedor")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contenedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 

    @NotBlank
    @Column(nullable=false, unique=true, length=40) // Ajustado a 40
    private String codigo; // ¡Cambiado de 'identificacion'!

    @NotNull
    @Column(name = "capacidad_kg", nullable = false)
    private Integer capacidadKg; // ¡Cambiado a Integer y renombrado!

    @NotBlank
    @Column(nullable=false, length=20)
    private String estado;
    
    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn; 

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
}