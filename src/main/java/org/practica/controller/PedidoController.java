package org.practica.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.practica.demo.Cliente;
import org.practica.demo.Comercial;
import org.practica.demo.Pedido;
import org.practica.service.ClienteService;
import org.practica.service.ComercialService;
import org.practica.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PedidoController {

	private final PedidoService pedidoService;
	private final ClienteService clienteService;
	private final ComercialService comercialService;

	public PedidoController(PedidoService pedidoService, ClienteService clienteService,
			ComercialService comercialService) {
		this.pedidoService = pedidoService;
		this.clienteService = clienteService;
		this.comercialService = comercialService;
	}

	@GetMapping("/pedidos")
	public String listar(Model model) {
		model.addAttribute("listaPedidos", pedidoService.listAll());
		model.addAttribute("nombresClientes", nombresClientes());
		model.addAttribute("nombresComerciales", nombresComerciales());
		return "pedidos";
	}

	@GetMapping("/pedidos/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("pedido", new Pedido());
		model.addAttribute("modoEdicion", false);
		cargarReferenciasFormulario(model);
		return "pedido-form";
	}

	@PostMapping("/pedidos")
	public String crear(@ModelAttribute Pedido pedido, Model model, RedirectAttributes redirectAttributes) {
		if (!referenciasExisten(pedido)) {
			return mostrarFormularioConError(pedido, false, model);
		}

		pedidoService.create(pedido);
		redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente.");
		return "redirect:/pedidos";
	}

	@GetMapping("/pedidos/editar/{id}")
	public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
		return pedidoService.find(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					model.addAttribute("modoEdicion", true);
					cargarReferenciasFormulario(model);
					return "pedido-form";
				})
				.orElseGet(() -> {
					redirectAttributes.addFlashAttribute("error", "No se encontró el pedido.");
					return "redirect:/pedidos";
				});
	}

	@PostMapping("/pedidos/{id}")
	public String actualizar(@PathVariable int id, @ModelAttribute Pedido pedido,
			Model model, RedirectAttributes redirectAttributes) {
		pedido.setId(id);
		if (!referenciasExisten(pedido)) {
			return mostrarFormularioConError(pedido, true, model);
		}

		pedidoService.update(pedido);
		redirectAttributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente.");
		return "redirect:/pedidos";
	}

	@GetMapping("/pedidos/{id}")
	public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
		return pedidoService.find(id)
				.map(pedido -> {
					model.addAttribute("pedido", pedido);
					model.addAttribute("nombreCliente", clienteService.find(pedido.getClienteId())
							.map(this::nombreCompleto).orElse("Cliente no disponible"));
					model.addAttribute("nombreComercial", comercialService.find(pedido.getComercialId())
							.map(this::nombreCompleto).orElse("Comercial no disponible"));
					return "pedido-detalle";
				})
				.orElseGet(() -> {
					redirectAttributes.addFlashAttribute("error", "No se encontró el pedido.");
					return "redirect:/pedidos";
				});
	}

	@PostMapping("/pedidos/borrar/{id}")
	public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
		pedidoService.delete(id);
		redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado correctamente.");
		return "redirect:/pedidos";
	}

	private void cargarReferenciasFormulario(Model model) {
		model.addAttribute("listaClientes", clienteService.listAll());
		model.addAttribute("listaComerciales", comercialService.listAll());
	}

	private Map<Integer, String> nombresClientes() {
		return clienteService.listAll().stream()
				.collect(Collectors.toMap(Cliente::getId, this::nombreCompleto));
	}

	private Map<Integer, String> nombresComerciales() {
		return comercialService.listAll().stream()
				.collect(Collectors.toMap(Comercial::getId, this::nombreCompleto));
	}

	private String nombreCompleto(Cliente cliente) {
		return nombreCompleto(cliente.getNombre(), cliente.getApellido1(), cliente.getApellido2());
	}

	private String nombreCompleto(Comercial comercial) {
		return nombreCompleto(comercial.getNombre(), comercial.getApellido1(), comercial.getApellido2());
	}

	private String nombreCompleto(String nombre, String apellido1, String apellido2) {
		return apellido2 == null || apellido2.isBlank()
				? nombre + " " + apellido1
				: nombre + " " + apellido1 + " " + apellido2;
	}

	private boolean referenciasExisten(Pedido pedido) {
		boolean clienteExiste = clienteService.find(pedido.getClienteId()).isPresent();
		boolean comercialExiste = comercialService.find(pedido.getComercialId()).isPresent();
		return clienteExiste && comercialExiste;
	}

	private String mostrarFormularioConError(Pedido pedido, boolean modoEdicion, Model model) {
		model.addAttribute("pedido", pedido);
		model.addAttribute("modoEdicion", modoEdicion);
		model.addAttribute("error", "Selecciona un cliente y un comercial válidos.");
		cargarReferenciasFormulario(model);
		return "pedido-form";
	}
}