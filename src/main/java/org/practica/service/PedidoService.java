package org.practica.service;

import java.util.List;
import java.util.Optional;

import org.practica.dao.PedidoDAO;
import org.practica.demo.Pedido;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

	private final PedidoDAO pedidoDAO;

	public PedidoService(PedidoDAO pedidoDAO) {
		this.pedidoDAO = pedidoDAO;
	}

	public List<Pedido> listAll() {
		return pedidoDAO.getAll();
	}

	public Optional<Pedido> find(int id) {
		return pedidoDAO.find(id);
	}

	public void create(Pedido pedido) {
		pedidoDAO.create(pedido);
	}

	public void update(Pedido pedido) {
		pedidoDAO.update(pedido);
	}

	public void delete(int id) {
		pedidoDAO.delete(id);
	}
}