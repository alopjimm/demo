package org.practica.service;

import java.util.List;

import org.practica.dao.ClienteDAO;
import org.practica.demo.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service 
public class ClienteService {
    
    private ClienteDAO clienteDAO;

    @Autowired 
    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listAll() {
        return clienteDAO.getAll();
    }

    public List<Cliente> getByciudad(String ciudad) {
        return clienteDAO.getByciudad(ciudad);
    }

    public java.util.Optional<Cliente> find(int id) {
        return clienteDAO.find(id);
    }

    public void create(Cliente cliente) {
        clienteDAO.create(cliente);
    }

    public void update(Cliente cliente) {
        clienteDAO.update(cliente);
    }

    public void delete(int id) {
        clienteDAO.delete(id);
    }
}
