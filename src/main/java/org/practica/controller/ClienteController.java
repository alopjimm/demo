package org.practica.controller;

import java.util.List;
import java.util.Comparator;
import java.time.LocalDate;

import org.practica.demo.Cliente;
import org.practica.service.ClienteService;
import org.practica.service.ComercialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    
@Controller 
public class ClienteController {
    private ClienteService clienteService;
    private ComercialService comercialService;

    @Autowired 
    public ClienteController(ClienteService clienteService, ComercialService comercialService) {
        this.clienteService = clienteService;
        this.comercialService = comercialService;
    }
    

    @GetMapping("/clientes")
    public String listar(@RequestParam(required = false) String ciudad, Model model) {
        List<Cliente> todosLosClientes = clienteService.listAll();
        List<String> ciudades = todosLosClientes.stream()
                .map(Cliente::getCiudad)
                .filter(nombreCiudad -> nombreCiudad != null && !nombreCiudad.isBlank())
                .distinct()
                .sorted(Comparator.comparing(String::toLowerCase))
                .toList();
        List<Cliente> listaClientes = ciudad == null || ciudad.isBlank()
                ? todosLosClientes
                : clienteService.getByciudad(ciudad);

        model.addAttribute("listaClientes", listaClientes);
        model.addAttribute("ciudades", ciudades);
        model.addAttribute("ciudadSeleccionada", ciudad);

        return "clientes";

    }

    @GetMapping("/clientes/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("modoEdicion", false);
        return "cliente-form";
    }

    @PostMapping("/clientes")
    public String crear(@ModelAttribute Cliente cliente, RedirectAttributes redirectAttributes) {
        clienteService.create(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente creado correctamente.");
        return "redirect:/clientes";
    }

    @GetMapping("/clientes/editar/{id}")
    public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.find(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    model.addAttribute("modoEdicion", true);
                    model.addAttribute("listaComerciales", comercialService.listAll());
                    model.addAttribute("comercialesVinculados", clienteService.getComercialesByClienteId(id));
                    return "cliente-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "No se encontró el cliente.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/clientes/{id}")
    public String actualizar(@PathVariable int id, @ModelAttribute Cliente cliente,
            RedirectAttributes redirectAttributes) {
        cliente.setId(id);
        clienteService.update(cliente);
        redirectAttributes.addFlashAttribute("mensaje", "Cliente actualizado correctamente.");
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/{id}/comerciales")
    public String agregarComercial(@PathVariable int id, @RequestParam int comercialId,
            @RequestParam LocalDate fecha, @RequestParam int prioridad,
            RedirectAttributes redirectAttributes) {
        try {
            clienteService.addComercial(id, comercialId, fecha, prioridad);
            redirectAttributes.addFlashAttribute("mensajeComercial", "Comercial añadido al cliente correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("errorComercial",
                    "No se pudo añadir: el cliente o el comercial no existe, o ya están asociados.");
        }
        return "redirect:/clientes/editar/" + id;
    }

    @PostMapping("/clientes/{id}/comerciales/{comercialId}/borrar")
    public String borrarComercial(@PathVariable int id, @PathVariable int comercialId) {
        clienteService.removeComercial(id, comercialId);
        return "redirect:/clientes/editar/" + id;
    }

    @GetMapping("/clientes/{id}")
    public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        return clienteService.find(id)
                .map(cliente -> {
                    model.addAttribute("cliente", cliente);
                    return "cliente-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "No se encontró el cliente.");
                    return "redirect:/clientes";
                });
    }

    @PostMapping("/clientes/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
            redirectAttributes.addFlashAttribute("mensaje", "Cliente eliminado correctamente.");
        } catch (DataIntegrityViolationException exception) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: el cliente tiene pedidos asociados.");
        }
        return "redirect:/clientes";
    }
    
}
