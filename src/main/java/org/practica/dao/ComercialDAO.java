package org.practica.dao;

import java.util.List;
import java.util.Optional;

import org.practica.demo.Comercial;

public interface ComercialDAO {
	public void create(Comercial comercial);
	public List<Comercial> getAll();
	public Optional<Comercial> find(int id);
	public void update(Comercial comercial);
	public void delete(int id);
}