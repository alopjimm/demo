package com.practica.demo;

import org.junit.jupiter.api.Test;
import org.practica.demo.DemoApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
class DemoApplicationTests {

	@Autowired
	private MockMvc mockMvc;

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

}
