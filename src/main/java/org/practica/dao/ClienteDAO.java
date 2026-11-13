package org.practica.dao;


import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import org.practica.demo.Cliente;
import org.practica.dto.ClienteComercialDTO;

public interface ClienteDAO {
	public void create(Cliente cliente);
	public List<Cliente> getAll();
	public List<Cliente> getByciudad(String ciudad);
	public Optional<Cliente>  find(int id);
	public void update(Cliente cliente);
	public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad);
	public List<ClienteComercialDTO> getComercialesByClienteId(int clienteId);
	public void removeComercial(int clienteId, int comercialId);
	public void delete(int id);
}