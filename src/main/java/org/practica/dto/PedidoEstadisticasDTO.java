package org.practica.dto;

import java.math.BigDecimal;

public record PedidoEstadisticasDTO(int cantidadPedidos, BigDecimal importeTotal, BigDecimal mediaPorPedido,
		BigDecimal importeMaximo, BigDecimal importeMinimo) {
}