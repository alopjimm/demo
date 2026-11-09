package org.practica.service;

import java.util.List;
import java.util.Optional;

import org.practica.dao.ComercialDAO;
import org.practica.demo.Comercial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.practica.demo.PedidoDetalle;

@Service
public class ComercialService {

	@Autowired 
	private  ComercialDAO comercialDAO;

	@Autowired
	private PedidoService pedidoDAO;

	public ComercialService(ComercialDAO comercialDAO) {
		this.comercialDAO = comercialDAO;
	}

	public List<Comercial> listAll() {
		return comercialDAO.getAll();
	}

	public Optional<Comercial> find(int id) {
		return comercialDAO.find(id);
	}

	public void create(Comercial comercial) {
		comercialDAO.create(comercial);
	}

	public void update(Comercial comercial) {
		comercialDAO.update(comercial);
	}

	public void delete(int id) {
		comercialDAO.delete(id);
	}

	public List<PedidoDetalle> findPedidosByComercial(int comercialId) {
		return pedidoDAO.findByComercialId(comercialId);
	}

}