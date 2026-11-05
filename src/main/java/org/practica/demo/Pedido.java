package org.practica.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

	private int id;
	private BigDecimal total;
	private LocalDate fecha;
	private int clienteId;
	private int comercialId;

}