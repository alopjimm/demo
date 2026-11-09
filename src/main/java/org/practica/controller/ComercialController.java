package org.practica.controller;

import org.practica.demo.Comercial;
import org.practica.service.ComercialService;
import org.practica.service.PedidoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComercialController {

	private final ComercialService comercialService;
	private final PedidoService pedidoService;

	public ComercialController(ComercialService comercialService, PedidoService pedidoService) {
		this.comercialService = comercialService;
		this.pedidoService = pedidoService;
	}

	@GetMapping("/comerciales")
	public String listar(Model model) {
		model.addAttribute("listaComerciales", comercialService.listAll());
		return "comerciales";
	}

	@GetMapping("/comerciales/nuevo")
	public String nuevo(Model model) {
		model.addAttribute("comercial", new Comercial());
		model.addAttribute("modoEdicion", false);
		return "comercial-form";
	}

	@PostMapping("/comerciales")
	public String crear(@ModelAttribute Comercial comercial, RedirectAttributes redirectAttributes) {
		comercialService.create(comercial);
		redirectAttributes.addFlashAttribute("mensaje", "Comercial creado correctamente.");
		return "redirect:/comerciales";
	}

	@GetMapping("/comerciales/editar/{id}")
	public String editar(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
		return comercialService.find(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					model.addAttribute("modoEdicion", true);
					return "comercial-form";
				})
				.orElseGet(() -> {
					redirectAttributes.addFlashAttribute("error", "No se encontró el comercial.");
					return "redirect:/comerciales";
				});
	}

	@PostMapping("/comerciales/{id}")
	public String actualizar(@PathVariable int id, @ModelAttribute Comercial comercial,
			RedirectAttributes redirectAttributes) {
		comercial.setId(id);
		comercialService.update(comercial);
		redirectAttributes.addFlashAttribute("mensaje", "Comercial actualizado correctamente.");
		return "redirect:/comerciales";
	}

	@GetMapping("/comerciales/{id}")
	public String detalle(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
		return comercialService.find(id)
				.map(comercial -> {
					model.addAttribute("comercial", comercial);
					model.addAttribute("listaPedidos", pedidoService.findByComercialId(id));
					return "comercial-detalle";
				})
				.orElseGet(() -> {
					redirectAttributes.addFlashAttribute("error", "No se encontró el comercial.");
					return "redirect:/comerciales";
				});
	}

	@PostMapping("/comerciales/borrar/{id}")
	public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
		try {
			comercialService.delete(id);
			redirectAttributes.addFlashAttribute("mensaje", "Comercial eliminado correctamente.");
		} catch (DataIntegrityViolationException exception) {
			redirectAttributes.addFlashAttribute("error", "No se puede eliminar: el comercial tiene pedidos asociados.");
		}
		return "redirect:/comerciales";
	}

	
}