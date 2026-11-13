package org.practica.service;

import java.util.List;
import java.time.LocalDate;

import org.practica.dao.ClienteDAO;
import org.practica.demo.Cliente;
import org.practica.dto.ClienteComercialDTO;
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

    public void addComercial(int clienteId, int comercialId, LocalDate fecha, int prioridad) {
        clienteDAO.addComercial(clienteId, comercialId, fecha, prioridad);
    }

    public List<ClienteComercialDTO> getComercialesByClienteId(int clienteId) {
        return clienteDAO.getComercialesByClienteId(clienteId);
    }

    public void removeComercial(int clienteId, int comercialId) {
        clienteDAO.removeComercial(clienteId, comercialId);
    }

    public void delete(int id) {
        clienteDAO.delete(id);
    }
}
