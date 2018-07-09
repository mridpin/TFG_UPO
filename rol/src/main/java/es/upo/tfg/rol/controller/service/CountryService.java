package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface CountryService {

	Country assembleCountry(User player, MultipartFile data);

	void saveCountry(Country country);

	List<Country> findCountries(Game game);

}
