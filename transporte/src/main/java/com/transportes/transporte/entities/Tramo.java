package com.transportes.transporte.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name="tramo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tramo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Relacion con la Solicitud ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_id", nullable = false)
    private Solicitud solicitud;

    @Column(nullable = false, length = 20)
    private String estado;

    // --- Apuntaria a nuestro microservicio de Operacion ---
    @Column(name = "camion_id")
    private Long camionId;

    @Column(name = "deposito_origen_id")
    private Long depositoOrigenId;

    @Column(name = "nombre_deposito_origen", length = 100)
    private String nombreDepositoOrigen;

    @Column(name = "deposito_destino_id")
    private Long depositoDestinoId;

    @Column(name = "nombre_deposito_destino", length = 100)
    private String nombreDepositoDestino;
    
    @Column(name = "origen_dir")
    private String origenDir;
    
    @Column(name = "destino_dir")
    private String destinoDir;

    @Column(name = "distancia_km", precision = 10, scale = 2)
    private BigDecimal distanciaKm;

    @Column(name = "duracion_min")
    private Integer duracionMin; 

    @Column(name = "costo_real_tramo", precision = 14, scale = 2)
    private BigDecimal costoRealTramo;

    @Column(name = "fecha_hora_inicio")
    private OffsetDateTime fechaHoraInicio; 

    @Column(name = "fecha_hora_fin")
    private OffsetDateTime fechaHoraFin; 
}