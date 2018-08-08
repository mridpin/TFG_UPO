package es.upo.tfg.rol.controller.service;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

public interface ScenarioService {
	
	Scenario createScenario(String name, String description, MultipartFile data, User user);
	
}
