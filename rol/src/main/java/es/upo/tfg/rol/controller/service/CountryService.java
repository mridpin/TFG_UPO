package es.upo.tfg.rol.controller.service;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.User;

public interface CountryService {

	Country assembleCountry(User player, MultipartFile data);
	
	void saveCountry(Country country);

}
