package org.practica.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comercial {

	private int id;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private float comision;

}