package com.operaciones.operacion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name="cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "razon_social", nullable = false, length = 120)
    private String razonSocial;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String cuit;

    @Column(length = 200)
    private String direccion;

    @Column(length = 40)
    private String telefono;

    @Column(length = 80)
    private String email;
}