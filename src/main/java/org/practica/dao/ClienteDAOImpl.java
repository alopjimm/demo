package org.practica.dao;


import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.practica.demo.Cliente;
import org.practica.dto.ClienteComercialDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

//Anotación lombok para logging (traza) de la aplicación
@Slf4j
//Un Repository es un componente y a su vez un estereotipo de Spring 
//que forma parte de la 'capa de persistencia'.
@Repository
public class ClienteDAOImpl implements ClienteDAO {

	 //Plantilla jdbc inyectada automáticamente por el framework Spring, gracias a la anotación @Autowired.
	 private final JdbcTemplate jdbcTemplate;

	ClienteDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/**
	 * Inserta en base de datos el nuevo Cliente, actualizando el id en el bean Cliente.
	 */
	@Override	
	public synchronized void create(Cliente cliente) {
		
							//Desde java15+ se tiene la triple quote """ para bloques de texto como cadenas.
		String sqlInsert = """
							INSERT INTO cliente (nombre, apellido1, apellido2, ciudad, categoría) 
							VALUES  (     ?,         ?,         ?,       ?,         ?)
						   """;
		
		
		//Sin recuperación de id generado
//		int rows = jdbcTemplate.update(sqlInsert,
//							cliente.getNombre(),
//							cliente.getApellido1(),
//							cliente.getApellido2(),
//							cliente.getCiudad(),
//							cliente.getCategoria()
//					);
		
		//Con recuperación de id generado
            KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[] { "id" });
			int idx = 1;
			ps.setString(idx++, cliente.getNombre());
			ps.setString(idx++, cliente.getApellido1());
			ps.setString(idx++, cliente.getApellido2());
			ps.setString(idx++, cliente.getCiudad());
			ps.setObject(idx, cliente.getCategoria());
			return ps;
		},keyHolder);
		
		if (keyHolder.getKey() != null) {
			cliente.setId(keyHolder.getKey().intValue());
		}

		log.info("Insertados {} registros.", rows);
	}

	/**
	 * Devuelve lista con todos los Clientes.
	 */
	@Override
	public List<Cliente> getAll() {
		
		List<Cliente> clientes = jdbcTemplate.query(
                "SELECT * FROM cliente",
                (rs, rowNum) -> mapCliente(rs)
        );
		
		log.info("Devueltos {} registros.", clientes.size());
		
        return clientes;
        
	}

	@Override
	public List<Cliente> getByciudad(String ciudad) {
		return jdbcTemplate.query(
				"SELECT * FROM cliente WHERE ciudad = ?",
				(rs, rowNum) -> mapCliente(rs),
				ciudad);
	}

	/**
	 * Devuelve Optional de Cliente con el ID dado.
	 */
	@Override
	public Optional<Cliente> find(int id) {
		
		List<Cliente> clientes = jdbcTemplate.query(
				"SELECT * FROM cliente WHERE id = ?",
				(rs, rowNum) -> mapCliente(rs),
				id);
		return clientes.stream().findFirst();
        
	}
	/**
	 * Actualiza Cliente con campos del bean Cliente según ID del mismo.
	 */
	@Override
	public void update(Cliente cliente) {
		
		int rows = jdbcTemplate.update("""
										UPDATE cliente SET 
														nombre = ?, 
														apellido1 = ?, 
														apellido2 = ?,
														ciudad = ?,
														categoría = ?  
												WHERE id = ?
										""", cliente.getNombre()
										, cliente.getApellido1()
										, cliente.getApellido2()
										, cliente.getCiudad()
										, cliente.getCategoria()
										, cliente.getId());
		
		log.info("Update de Cliente con {} registros actualizados.", rows);
    
	}

	@Override
	public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
		jdbcTemplate.update("""
				INSERT INTO cliente_has_comercial (cliente_id, comercial_id, fecha, prioridad)
				VALUES (?, ?, ?, ?)
				""", clienteId, comercialId, fecha, prioridad);
	}

	@Override
	public List<ClienteComercialDTO> getComercialesByClienteId(int clienteId) {
		return jdbcTemplate.query("""
				SELECT co.id, CONCAT_WS(' ', co.nombre, co.apellido1, co.apellido2) AS nombre_completo,
				       chc.fecha, chc.prioridad
				FROM cliente_has_comercial chc
				JOIN comercial co ON co.id = chc.comercial_id
				WHERE chc.cliente_id = ?
				ORDER BY chc.prioridad, co.apellido1, co.nombre
				""", (rs, rowNum) -> new ClienteComercialDTO(
					rs.getInt("id"),
					rs.getString("nombre_completo"),
					rs.getDate("fecha") == null ? null : rs.getDate("fecha").toLocalDate(),
					rs.getInt("prioridad")),
				clienteId);
	}

	@Override
	public void removeComercial(int clienteId, int comercialId) {
		jdbcTemplate.update("""
				DELETE FROM cliente_has_comercial
				WHERE cliente_id = ? AND comercial_id = ?
				""", clienteId, comercialId);
	}

	/**
	 * Borra Cliente con ID proporcionado.
	 */
	@Override
	public void delete(int id) {
		
		int rows = jdbcTemplate.update("DELETE FROM cliente WHERE id = ?", id);
		
		log.info("Delete de Cliente con {} registros eliminados.", rows);		
	}
		

	private Cliente mapCliente(java.sql.ResultSet rs) throws java.sql.SQLException {
		return new Cliente(
				rs.getInt("id"),
				rs.getString("nombre"),
				rs.getString("apellido1"),
				rs.getString("apellido2"),
				rs.getString("ciudad"),
				rs.getObject("categoría", Integer.class));
	}
	
}
