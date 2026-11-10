package org.practica.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.practica.dao.PedidoDAO;
import org.practica.demo.Pedido;
import org.practica.demo.PedidoDetalle;
import org.practica.dto.ClienteTotalPedidosDTO;
import org.practica.dto.PedidoEstadisticasDTO;
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

	public List<PedidoDetalle> findByComercialId(int comercialId) {
		return pedidoDAO.findByComercialId(comercialId);
	}

	public PedidoEstadisticasDTO calcularEstadisticas(List<PedidoDetalle> pedidos) {
		BigDecimal importeTotal = pedidos.stream()
				.map(PedidoDetalle::getTotal)
				.filter(total -> total != null)
				.reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(2, RoundingMode.HALF_UP);
		BigDecimal mediaPorPedido = pedidos.isEmpty()
				? BigDecimal.ZERO
				: importeTotal.divide(BigDecimal.valueOf(pedidos.size()), 2, RoundingMode.HALF_UP);
		BigDecimal importeMaximo = pedidos.stream()
				.map(PedidoDetalle::getTotal)
				.filter(total -> total != null)
				.max(BigDecimal::compareTo)
				.map(total -> total.setScale(2, RoundingMode.HALF_UP))
				.orElse(null);
		BigDecimal importeMinimo = pedidos.stream()
				.map(PedidoDetalle::getTotal)
				.filter(total -> total != null)
				.min(BigDecimal::compareTo)
				.map(total -> total.setScale(2, RoundingMode.HALF_UP))
				.orElse(null);

		return new PedidoEstadisticasDTO(pedidos.size(), importeTotal, mediaPorPedido, importeMaximo, importeMinimo);
	}

	public List<ClienteTotalPedidosDTO> resumirClientesPorTotal(List<PedidoDetalle> pedidos) {
		Map<Integer, ClienteTotalPedidosDTO> totalesPorCliente = new HashMap<>();
		for (PedidoDetalle pedido : pedidos) {
			BigDecimal importePedido = pedido.getTotal() == null ? BigDecimal.ZERO : pedido.getTotal();
			totalesPorCliente.compute(pedido.getClienteId(), (clienteId, resumen) -> {
				BigDecimal importeAnterior = resumen == null ? BigDecimal.ZERO : resumen.importeTotal();
				String clienteNombre = resumen == null ? pedido.getClienteNombre() : resumen.clienteNombre();
				return new ClienteTotalPedidosDTO(clienteId, clienteNombre,
						importeAnterior.add(importePedido).setScale(2, RoundingMode.HALF_UP));
			});
		}

		return totalesPorCliente.values().stream()
				.sorted(Comparator.comparing(ClienteTotalPedidosDTO::importeTotal).reversed()
						.thenComparing(ClienteTotalPedidosDTO::clienteNombre,
								Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
						.thenComparingInt(ClienteTotalPedidosDTO::clienteId))
				.toList();
	}
}