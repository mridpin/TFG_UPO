package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.dao.CountryRepository;
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
		// TODO: Raise exceptions with non valid csvs or non utf8 ones
		Country country = new Country();
		country.setPlayer(player);
		// Parse CSV file
		String line;
		String subscenario;
		String type;
		// List<List<Double>> attributeList = new ArrayList<List<Double>>();
		// List<Double> subscenarioAttributeList = new ArrayList<Double>();
		Map<String, Map<String, Map<String, Double>>> attributes = new HashMap<>();
		Map<String, Map<String, Double>> subscenarioAttributes = new HashMap<>();
		Map<String, Double> typeAttributes = new HashMap<>();

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
					subscenario = dataline[1];
					// subscenarioAttributeList = new ArrayList<Double>();
					subscenarioAttributes = new HashMap<>();
					// attributeList.add(subscenarioAttributeList);
					attributes.put(subscenario, subscenarioAttributes);
					break;
				case "type":
					type = dataline[1];
					typeAttributes = new HashMap<>();
					subscenarioAttributes.put(type, typeAttributes);
					break;
				default:
					// subscenarioAttributeList.add(Double.parseDouble(dataline[1]));
					typeAttributes.put(dataline[0], Double.parseDouble(dataline[1]));
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// country.setAttributes(attributeList);
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

	@Override
	public Map<String, Map<String, Map<String, Double>>> mapCountry(Country country) {
		String line = null;
		String subscenario;
		String type;
		Map<String, Map<String, Map<String, Double>>> attributes = new HashMap<>();
		Map<String, Map<String, Double>> subscenarioAttributes = new HashMap<>();
		Map<String, Double> typeAttributes = new HashMap<>();
		try {
			String filename = "countryData" + File.separator + country.getData();
			InputStream is = new FileInputStream(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				String whatDo = dataline[0].trim().toLowerCase();
				switch (whatDo) {
				case "nombre":
					country.setName(dataline[1]);
					break;
				case "subescenario":
					subscenario = dataline[1];
					// subscenarioAttributeList = new ArrayList<Double>();
					subscenarioAttributes = new HashMap<>();
					// attributeList.add(subscenarioAttributeList);
					attributes.put(subscenario, subscenarioAttributes);
					break;
				case "tipo":
					type = dataline[1];
					typeAttributes = new HashMap<>();
					subscenarioAttributes.put(type, typeAttributes);
					break;
				default:
					// subscenarioAttributeList.add(Double.parseDouble(dataline[1]));
					if ("true".equals(dataline[1]) || "sí".equals(dataline[1])
							|| "si".equals(dataline[1]) || "yes".equals(dataline[1])) {
						typeAttributes.put(dataline[0], 1.0);
					} else if ("false".equals(dataline[1])) {
						typeAttributes.put(dataline[0], 0.0);
					} else {
						typeAttributes.put(dataline[0], Double.parseDouble(dataline[1]));
						break;
					}
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Throw a file corruption exception and ask to manually upload the
			// country file again (priority=3)
		} catch (NumberFormatException e) {
			System.err.print(country.getName());
			e.printStackTrace();
		}
		return attributes;
	}

}
