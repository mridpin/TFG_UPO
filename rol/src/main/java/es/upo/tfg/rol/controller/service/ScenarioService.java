package es.upo.tfg.rol.controller.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;

public interface ScenarioService {
	
	/**
	 * Creates a scenario from the form attributes
	 * @param name: name of the scenario
	 * @param description: keywords to help in the search
	 * @param data: csv file with attributes and their weight
	 * @param user: author of the scenario
	 * @return the created scenario
	 */
	Scenario createScenario(String name, String description, MultipartFile data, User user);

	/**
	 * Process the datafile of a scenario, returning it as a map of maps to be useable to the client.
	 * If the file is incorrect, it will return null
	 * 
	 * @param scenario we want to map
	 * @return map of the attributes of the scenario
	 */
	Map<String, Map<String, Map<String, Double>>> mapScenario(Scenario scenario);

	/**
	 * Finds all available scenarios
	 * @return a list of scenarios
	 */
	List<Scenario> findAllScenarios();

	/**
	 * Finds a scenario by id
	 * @param id
	 * @return scenario
	 */
	Scenario findById(Long id);

	/**
	 * Reads the file of the scenario and returns a map with the rules
	 * @param scenario 
	 * @return map of rules 
	 */
	Map<String, Object> mapRules(Scenario scenario);

	List<String> validateScenarioFile(MultipartFile data);

	Scenario save(Scenario scenario);
	
}
