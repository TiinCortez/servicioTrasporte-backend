package com.transportes.transporte.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name="solicitud")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero_solicitud", unique = true, nullable = false, length = 40)
    private String numeroSolicitud;

    // --- Referencias "blandas" a MS Operacion ---
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @Column(name = "contenedor_id", nullable = false)
    private Long contenedorId;

    // --- Datos del viaje ---
    @Column(name = "origen_lat")
    private Double origenLat;
    @Column(name = "origen_lng")
    private Double origenLng;
    @Column(name = "origen_dir", length = 200)
    private String origenDir;

    @Column(name = "destino_lat")
    private Double destinoLat;
    @Column(name = "destino_lng")
    private Double destinoLng;
    @Column(name = "destino_dir", length = 200)
    private String destinoDir;

    // --- Estado y Costos ---
    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "costo_estimado", precision = 14, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "tiempo_estimado_min")
    private Integer tiempoEstimadoMin;

    @Column(name = "costo_final", precision = 14, scale = 2)
    private BigDecimal costoFinal;

    @Column(name = "tiempo_real_min")
    private Integer tiempoRealMin;

    @CreationTimestamp
    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    // 🔥🔥🔥 ESTO ES LO QUE TE FALTABA 🔥🔥🔥
    @OneToMany(mappedBy = "solicitud", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Tramo> tramos;
}