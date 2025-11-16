package com.seguimiento.seguimiento.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "evento_seguimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ej: SOLICITUD_CREADA, INICIO_TRAMO, FIN_TRAMO, ENTREGADO
    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(name = "solicitud_id", nullable = false)
    private Long solicitudId;

    @Column(name = "tramo_id")
    private Long tramoId;

    @Column(name = "fecha_hora", nullable = false)
    private OffsetDateTime fechaHora;

    @Column(length = 300)
    private String descripcion;

    private Double lat;
    private Double lng;

    @PrePersist
    public void prePersist() {
        if (fechaHora == null) {
            fechaHora = OffsetDateTime.now();
        }
    }
}
