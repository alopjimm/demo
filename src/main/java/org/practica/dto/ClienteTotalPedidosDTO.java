package org.practica.dto;

import java.math.BigDecimal;

public record ClienteTotalPedidosDTO(int clienteId, String clienteNombre, BigDecimal importeTotal) {
}