package es.upo.tfg.rol.controller.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

public interface CountryService {

	/**
	 * Assembles a country with the data from the form
	 * 
	 * @param player
	 *            submitted in the form
	 * @param data
	 *            file with the attributes and values of the country
	 * @return a country object
	 */
	Country assembleCountry(User player, MultipartFile data);

	/**
	 * Using the datafile from a scenario, validates the datafile from a country to
	 * make sure they match
	 * 
	 * @param scenario
	 *            for the game that is being created
	 * @param data
	 *            of the country that we are trying to add to the game
	 * @return a list of error messages, if any errors were found, or an empty list
	 *         otherwise
	 */
	List<String> validateCountryFile(Scenario scenario, MultipartFile data);

	/**
	 * Persists a country in the database
	 * 
	 * @param country
	 *            to persist
	 */
	void saveCountry(Country country);

	/**
	 * Returns a list of countries in a game
	 * 
	 * @param game
	 *            to look countries in
	 * @return a list of countries added to a game, or an empty list if there aren't
	 *         any
	 */
	List<Country> findCountries(Game game);

	/**
	 * Maps all the attributes of a country as a three-nested map using the country
	 * file
	 * 
	 * @param c
	 *            country to map
	 * @return the map of attributes
	 */
	Map<String, Map<String, Map<String, Double>>> mapCountry(Country c);

	/**
	 * Writes the file of a country according to the map provided
	 * @param attributes of the country
	 * @param country to demap
	 */
	void demapCountry(Map<String, Map<String, Map<String, Double>>> attributes,
			Country country);

}
