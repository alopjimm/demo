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
}
