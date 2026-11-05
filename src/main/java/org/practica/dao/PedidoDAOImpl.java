package org.practica.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.practica.demo.Pedido;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class PedidoDAOImpl implements PedidoDAO {

	private final JdbcTemplate jdbcTemplate;

	PedidoDAOImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public synchronized void create(Pedido pedido) {
		String sqlInsert = """
				INSERT INTO pedido (total, fecha, id_cliente, id_comercial)
				VALUES (?, ?, ?, ?)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();
		int rows = jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sqlInsert, new String[] { "id" });
			ps.setBigDecimal(1, pedido.getTotal());
			ps.setObject(2, pedido.getFecha());
			ps.setInt(3, pedido.getClienteId());
			ps.setInt(4, pedido.getComercialId());
			return ps;
		}, keyHolder);

		if (keyHolder.getKey() != null) {
			pedido.setId(keyHolder.getKey().intValue());
		}

		log.info("Insertados {} registros de pedido.", rows);
	}

	@Override
	public List<Pedido> getAll() {
		List<Pedido> pedidos = jdbcTemplate.query(
				"SELECT id, total, fecha, id_cliente, id_comercial FROM pedido",
				(rs, rowNum) -> mapPedido(rs));

		log.info("Devueltos {} registros de pedido.", pedidos.size());
		return pedidos;
	}

	@Override
	public Optional<Pedido> find(int id) {
		List<Pedido> pedidos = jdbcTemplate.query(
				"SELECT id, total, fecha, id_cliente, id_comercial FROM pedido WHERE id = ?",
				(rs, rowNum) -> mapPedido(rs),
				id);

		return pedidos.stream().findFirst();
	}

	@Override
	public void update(Pedido pedido) {
		int rows = jdbcTemplate.update("""
				UPDATE pedido SET
					total = ?,
					fecha = ?,
					id_cliente = ?,
					id_comercial = ?
				WHERE id = ?
				""",
				pedido.getTotal(),
				pedido.getFecha(),
				pedido.getClienteId(),
				pedido.getComercialId(),
				pedido.getId());

		log.info("Update de pedido con {} registros actualizados.", rows);
	}

	@Override
	public void delete(int id) {
		int rows = jdbcTemplate.update("DELETE FROM pedido WHERE id = ?", id);
		log.info("Delete de pedido con {} registros eliminados.", rows);
	}

	private Pedido mapPedido(ResultSet rs) throws SQLException {
		java.sql.Date fecha = rs.getDate("fecha");
		return new Pedido(
				rs.getInt("id"),
				rs.getBigDecimal("total"),
				fecha == null ? null : fecha.toLocalDate(),
				rs.getInt("id_cliente"),
				rs.getInt("id_comercial"));
	}
}