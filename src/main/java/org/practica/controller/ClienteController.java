package org.practica.controller;

import java.util.List;

import org.practica.demo.Cliente;
import org.practica.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class ClienteController {
    private ClienteService clienteService;

    @Autowired 
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }
    

    @GetMapping("/clientes")
    public String listar(Model model) {
        
        List<Cliente> listaClientes = clienteService.listAll();
        model.addAttribute("listaClientes", listaClientes);

        return "clientes";

    }
    
}
