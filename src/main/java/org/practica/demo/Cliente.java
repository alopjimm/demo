package org.practica.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
//La anotación @Data de lombok proporcionará el código de: 
//getters/setters, toString, equals y hashCode
//propio de los objetos POJOS o tipo Beans
@Data
@NoArgsConstructor
@AllArgsConstructor 
public class Cliente {
	
	private int id;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String ciudad;
	private int categoria;
	
}