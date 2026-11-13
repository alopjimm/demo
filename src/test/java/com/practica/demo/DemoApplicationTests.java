package com.practica.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.practica.demo.Cliente;
import org.practica.demo.Comercial;
import org.practica.demo.DemoApplication;
import org.practica.demo.Pedido;
import org.practica.dto.ClienteTotalPedidosDTO;
import org.practica.dto.PedidoEstadisticasDTO;
import org.practica.service.ClienteService;
import org.practica.service.ComercialService;
import org.practica.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ClienteService clienteService;

	@Autowired
	private ComercialService comercialService;

	@Autowired
	private PedidoService pedidoService;

	@Test
	void contextLoads() {
	}

	@Test
	void rendersCustomerAndSalespersonPages() throws Exception {
		mockMvc.perform(get("/clientes"))
				.andExpect(status().isOk())
				.andExpect(view().name("clientes"));

		mockMvc.perform(get("/comerciales"))
				.andExpect(status().isOk())
				.andExpect(view().name("comerciales"));

		mockMvc.perform(get("/clientes/nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("cliente-form"));

		mockMvc.perform(get("/comerciales/nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("comercial-form"));

		mockMvc.perform(get("/clientes/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("cliente-detalle"));

		mockMvc.perform(get("/comerciales/1"))
				.andExpect(status().isOk())
				.andExpect(view().name("comercial-detalle"));
	}

	@Test
	@Transactional
	void rendersCustomerEditFormWithLinkedSalespeople() throws Exception {
		Cliente cliente = crearCliente("Eva", "Prueba", null);
		Comercial comercial = crearComercial("Luis", "Ventas", null);
		clienteService.addComercial(cliente.getId(), comercial.getId(), LocalDate.of(2024, 6, 15), 1);

		mockMvc.perform(get("/clientes/editar/{id}", cliente.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("cliente-form"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Añadir Comercial")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Luis Ventas")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("15/06/2024")));
	}

	@Test
	@Transactional
	void createsEditsAndRendersOrdersWithFullNamesAndSelectedReferences() throws Exception {
		Cliente cliente = crearCliente("Lucia", "SinSegundo", null);
		Comercial comercial = crearComercial("Carlos", "SinSegundo", null);
		Cliente clienteEditado = crearCliente("Marta", "Luna", "Prieto");
		Comercial comercialEditado = crearComercial("Pablo", "Ruiz", "Canto");

		mockMvc.perform(get("/pedidos/nuevo"))
				.andExpect(status().isOk())
				.andExpect(view().name("pedido-form"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Lucia SinSegundo")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Carlos SinSegundo")));

		mockMvc.perform(post("/pedidos")
				.param("total", "325.50")
				.param("fecha", "2024-06-15")
				.param("clienteId", Integer.toString(cliente.getId()))
				.param("comercialId", Integer.toString(comercial.getId())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pedidos"));

		Optional<Pedido> pedidoCreado = pedidoService.listAll().stream()
				.filter(pedido -> pedido.getClienteId() == cliente.getId()
						&& pedido.getComercialId() == comercial.getId())
				.findFirst();
		assertThat(pedidoCreado).isPresent();
		assertThat(pedidoCreado.orElseThrow().getTotal()).isEqualByComparingTo("325.50");
		int pedidoId = pedidoCreado.orElseThrow().getId();

		MvcResult formularioEdicion = mockMvc.perform(get("/pedidos/editar/{id}", pedidoId))
				.andExpect(status().isOk())
				.andExpect(view().name("pedido-form"))
				.andReturn();
		assertOpcionSeleccionada(formularioEdicion.getResponse().getContentAsString(), "clienteId", cliente.getId());
		assertOpcionSeleccionada(formularioEdicion.getResponse().getContentAsString(), "comercialId", comercial.getId());

		mockMvc.perform(post("/pedidos/{id}", pedidoId)
				.param("total", "410.75")
				.param("fecha", "2024-07-20")
				.param("clienteId", Integer.toString(clienteEditado.getId()))
				.param("comercialId", Integer.toString(comercialEditado.getId())))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/pedidos"));

		Pedido pedidoActualizado = pedidoService.find(pedidoId).orElseThrow();
		assertThat(pedidoActualizado.getTotal()).isEqualByComparingTo("410.75");
		assertThat(pedidoActualizado.getClienteId()).isEqualTo(clienteEditado.getId());
		assertThat(pedidoActualizado.getComercialId()).isEqualTo(comercialEditado.getId());

		mockMvc.perform(get("/pedidos"))
				.andExpect(status().isOk())
				.andExpect(view().name("pedidos"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Marta Luna Prieto")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pablo Ruiz Canto")));

		mockMvc.perform(get("/pedidos/{id}", pedidoId))
				.andExpect(status().isOk())
				.andExpect(view().name("pedido-detalle"))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Marta Luna Prieto")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pablo Ruiz Canto")));
	}

	@Test
	@Transactional
	void salespersonDetailShowsOnlyTheirOrders() throws Exception {
		Cliente cliente = crearCliente("Ana", "Lopez", null);
		Cliente segundoCliente = crearCliente("Sofia", "Mendez", null);
		Comercial comercial = crearComercial("Carlos", "Vega", null);
		Comercial otroComercial = crearComercial("Marta", "Rios", null);
		Pedido pedido = new Pedido(0, new BigDecimal("125.50"), LocalDate.of(2024, 6, 15),
				cliente.getId(), comercial.getId());
		pedidoService.create(pedido);
		Pedido segundoPedido = new Pedido(0, new BigDecimal("74.50"), LocalDate.of(2024, 6, 17),
				cliente.getId(), comercial.getId());
		pedidoService.create(segundoPedido);
		pedidoService.create(new Pedido(0, new BigDecimal("125.00"), LocalDate.of(2024, 6, 18),
				segundoCliente.getId(), comercial.getId()));
		pedidoService.create(new Pedido(0, new BigDecimal("125.00"), LocalDate.of(2024, 6, 19),
				segundoCliente.getId(), comercial.getId()));
		Pedido pedidoAjeno = new Pedido(0, new BigDecimal("80.00"), LocalDate.of(2024, 6, 16),
				cliente.getId(), otroComercial.getId());
		pedidoService.create(pedidoAjeno);

		mockMvc.perform(get("/comerciales/{id}", comercial.getId()))
				.andExpect(status().isOk())
				.andExpect(view().name("comercial-detalle"))
				.andExpect(model().attribute("estadisticasPedidos",
						new PedidoEstadisticasDTO(4, new BigDecimal("450.00"), new BigDecimal("112.50"),
								new BigDecimal("125.50"), new BigDecimal("74.50"))))
				.andExpect(model().attribute("clientesPorTotalPedidos", List.of(
						new ClienteTotalPedidosDTO(segundoCliente.getId(), "Sofia Mendez", new BigDecimal("250.00")),
						new ClienteTotalPedidosDTO(cliente.getId(), "Ana Lopez", new BigDecimal("200.00")))))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("125.50")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("200.00")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Clientes por total de pedidos")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("250.00")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("112.50")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pedido-maximo")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("pedido-minimo")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pedido de importe máximo")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Pedido de importe mínimo")))
				.andExpect(content().string(org.hamcrest.Matchers.containsString("Ana Lopez")))
				.andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("80.00"))));
	}

	@Test
	@Transactional
	void rejectsOrdersWithUnknownCustomerOrSalespersonIds() throws Exception {
		Cliente cliente = crearCliente("Valido", "Cliente", null);
		Comercial comercial = crearComercial("Valido", "Comercial", null);
		String idInexistente = "2147483647";

		mockMvc.perform(post("/pedidos")
				.param("total", "10.00")
				.param("fecha", "2024-06-15")
				.param("clienteId", idInexistente)
				.param("comercialId", Integer.toString(comercial.getId())))
				.andExpect(status().isOk())
				.andExpect(view().name("pedido-form"))
				.andExpect(model().attribute("error", "Selecciona un cliente y un comercial válidos."));

		mockMvc.perform(post("/pedidos")
				.param("total", "10.00")
				.param("fecha", "2024-06-15")
				.param("clienteId", Integer.toString(cliente.getId()))
				.param("comercialId", idInexistente))
				.andExpect(status().isOk())
				.andExpect(view().name("pedido-form"))
				.andExpect(model().attribute("error", "Selecciona un cliente y un comercial válidos."));

		assertThat(pedidoService.listAll()).noneMatch(pedido -> pedido.getClienteId() == cliente.getId());
	}

	@Test
	@Transactional
	void foreignKeysPreventDeletingCustomersAndSalespeopleWithOrders() throws Exception {
		Cliente cliente = crearCliente("Protegido", "Cliente", null);
		Comercial comercial = crearComercial("Protegido", "Comercial", null);
		Pedido pedido = new Pedido(0, new BigDecimal("25.00"), LocalDate.of(2024, 6, 15),
				cliente.getId(), comercial.getId());
		pedidoService.create(pedido);

		mockMvc.perform(post("/clientes/borrar/{id}", cliente.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/clientes"))
				.andExpect(flash().attribute("error",
						"No se puede eliminar: el cliente tiene pedidos asociados."));

		mockMvc.perform(post("/comerciales/borrar/{id}", comercial.getId()))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/comerciales"))
				.andExpect(flash().attribute("error",
						"No se puede eliminar: el comercial tiene pedidos asociados."));

		assertThat(clienteService.find(cliente.getId())).isPresent();
		assertThat(comercialService.find(comercial.getId())).isPresent();
		assertThat(pedidoService.find(pedido.getId())).isPresent();
	}

	private Cliente crearCliente(String nombre, String apellido1, String apellido2) {
		Cliente cliente = new Cliente(0, nombre, apellido1, apellido2, "Madrid", null);
		clienteService.create(cliente);
		return cliente;
	}

	private Comercial crearComercial(String nombre, String apellido1, String apellido2) {
		Comercial comercial = new Comercial(0, nombre, apellido1, apellido2, 0.15f);
		comercialService.create(comercial);
		return comercial;
	}

	private void assertOpcionSeleccionada(String html, String selectId, int id) {
		int selectStart = html.indexOf("id=\"" + selectId + "\"");
		int selectEnd = html.indexOf("</select>", selectStart);
		assertThat(selectStart).isGreaterThanOrEqualTo(0);
		assertThat(selectEnd).isGreaterThan(selectStart);

		String selectHtml = html.substring(selectStart, selectEnd);
		Pattern selectedOption = Pattern.compile(
				"<option(?=[^>]*\\bvalue=\"" + id + "\")(?=[^>]*\\bselected(?:=|\\s|>))[^>]*>");
		assertThat(selectedOption.matcher(selectHtml).find()).as(selectId + " debe conservar el ID seleccionado").isTrue();
	}

}
