package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.dao.CountryRepository;
import es.upo.tfg.rol.model.dao.UserRepository;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

@Service("countryService")
@Transactional
public class CountryServiceImpl implements CountryService {
	
	@Autowired
	private CountryRepository countryRep;

	@Override
	public Country assembleCountry(User player, MultipartFile data) {
		Country country = new Country();
		country.setPlayer(player);
		// Parse CSV file
		String line;
		String subscenario;
		Map<String, Map<String, Double>> attributes = new HashMap<>();
		Map<String, Double> subscenarioAttributes = new HashMap<>();
		
		try {
			InputStream is = data.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				String whatDo = dataline[0].trim().toLowerCase();
				switch (whatDo) {
				case "nombre":
					country.setName(dataline[1]);
					break;
				case "subescenario":
					subscenario=dataline[1];
					subscenarioAttributes = new HashMap<>();
					attributes.put(subscenario, subscenarioAttributes);
					break;
				default:
					subscenarioAttributes.put(dataline[0], Double.parseDouble(dataline[1]));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		country.setAttributes(attributes);
		return country;
	}

	@Override
	public void saveCountry(Country country) {
		countryRep.save(country);		
	}

	@Override
	public List<Country> findCountries(Game game) {		
		return countryRep.findByGame(game);
	}

}
