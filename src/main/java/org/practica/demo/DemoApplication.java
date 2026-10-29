package org.practica.demo;

import java.util.Optional;

import org.practica.dao.ClienteDAO;
import org.practica.dao.ComercialDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Slf4j 
@SpringBootApplication( scanBasePackages = "org.practica")
public class DemoApplication implements CommandLineRunner {

	@Autowired 
	private ClienteDAO clienteDAO;

	@Autowired
	private ComercialDAO comercialDAO;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@GetMapping("/cliente")
	public String home() {
		return "Hola, clientes!";
	}

	@Override 
	public void run(String... args) throws Exception {
		log.info("**********************");
		log.info("**Prueba de arranque ClienteDAO**");
		log.info("**********************");

		clienteDAO.getAll().forEach(c -> log.info("Cliente: {}", c));

		int id = 1;
		Optional<Cliente> cliente = clienteDAO.find(id);
		
			if (cliente.isPresent()) {
    		log.info("Cliente: {}, {}", id, cliente.get());
			
    		String nombreOld = cliente.get().getNombre();
			
    		cliente.get().setNombre("Jose M");
			
    		clienteDAO.update(cliente.get());
			
			clienteDAO.update(cliente.get());

			log.info("Cliente con id {} actualizado: {}", id, clienteDAO.find(id).get());
			
			clienteDAO.getAll().forEach(client -> log.info("Cliente: {}", client));

			cliente.get().setNombre(nombreOld);

			clienteDAO.update(cliente.get());
			
			log.info("Cliente con id {} restaurado: {}", id, clienteDAO.find(id).get());
		}

		log.info("**********************");
		log.info("**Prueba de arranque ComercialDAO**");
		log.info("**********************");
		comercialDAO.getAll().forEach(comercial -> log.info("Comercial: {}", comercial));

		Comercial comercialPrueba = new Comercial(0, "Prueba", "DAO", "Temporal", 0.1f);
		comercialDAO.create(comercialPrueba);
		try {
			int comercialId = comercialPrueba.getId();
			Optional<Comercial> encontrado = comercialDAO.find(comercialId);
			log.info("Comercial creado y consultado: {}", encontrado.orElse(null));

			if (encontrado.isPresent()) {
				Comercial comercialActualizado = encontrado.get();
				comercialActualizado.setNombre("Prueba actualizada");
				comercialActualizado.setComision(0.2f);
				comercialDAO.update(comercialActualizado);
				log.info("Comercial actualizado: {}", comercialDAO.find(comercialId).orElse(null));
			}
		} finally {
			comercialDAO.delete(comercialPrueba.getId());
		}
	}

}
