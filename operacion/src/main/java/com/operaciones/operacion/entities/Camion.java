package com.operaciones.operacion.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name="camion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String dominio;

    @Column(name = "nombre_transportista", length = 120)
    private String nombreTransportista;

    @Column(length = 40)
    private String telefono;

    @NotNull
    @Column(name = "cap_peso_kg", nullable = false, precision = 12, scale = 2)
    private BigDecimal capPesoKg;

    @NotNull
    @Column(name = "cap_vol_m3", nullable = false, precision = 12, scale = 2)
    private BigDecimal capVolM3;

    @NotNull
    @Column(name = "costo_base_km", nullable = false, precision = 12, scale = 2)
    private BigDecimal costoBaseKm;

    @NotNull
    @Column(name = "consumo_100km", nullable = false, precision = 8, scale = 3)
    private BigDecimal consumo100km;

    @NotNull
    @Builder.Default
    @Column(nullable = false)
    private Boolean disponible = true;
}