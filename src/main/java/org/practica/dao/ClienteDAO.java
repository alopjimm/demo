package org.practica.dao;


import java.util.List;
import java.util.Optional;
import org.practica.demo.Cliente;

public interface ClienteDAO {
	public void create(Cliente cliente);
	public List<Cliente> getAll();
	public List<Cliente> getByciudad(String ciudad);
	public Optional<Cliente>  find(int id);
	public void update(Cliente cliente);
	public void delete(int id);
}