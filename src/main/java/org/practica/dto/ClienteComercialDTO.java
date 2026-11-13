package org.practica.dto;

import java.time.LocalDate;

public record ClienteComercialDTO(int comercialId, String nombreCompleto, LocalDate fecha, int prioridad) {
}