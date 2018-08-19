package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.model.dao.CountryRepository;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
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
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8));
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

	@Override
	public void demapCountry(Map<String, Map<String, Map<String, Double>>> attributes,
			Country country) {
		String filename = "countryData" + File.separator + country.getData();
		try (OutputStreamWriter os = new OutputStreamWriter(
				new FileOutputStream(filename, false), StandardCharsets.UTF_8)) {
			String nameline = Rules.COUNTRY_NAME + Rules.SEMICOLON + country.getName()
					+ Rules.SEMICOLON + System.lineSeparator();
			os.write(nameline);
			for (String subscenario : attributes.keySet()) {
				Map<String, Map<String, Double>> subscenarioAttributes = attributes
						.get(subscenario);
				String subscenarioline = Rules.COUNTRY_SUBSCENARIO + Rules.SEMICOLON
						+ subscenario + Rules.SEMICOLON + System.lineSeparator();
				os.write(subscenarioline);
				for (String type : subscenarioAttributes.keySet()) {
					Map<String, Double> typeAttributes = subscenarioAttributes.get(type);
					String typeline = Rules.COUNTRY_TYPE + Rules.SEMICOLON + type
							+ Rules.SEMICOLON + System.lineSeparator();
					os.write(typeline);
					for (String key : typeAttributes.keySet()) {
						String value = key + Rules.SEMICOLON + typeAttributes.get(key)
								+ Rules.SEMICOLON + System.lineSeparator();
						os.write(value);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> validateCountryFile(Scenario scenario, MultipartFile data) {
		List<String> res = new ArrayList<>();
		// First, parse the scenario file and save its attribute names in a list, to
		// compare them later
		List<String> scenarioFile = new ArrayList<>();
		String filename = Rules.SCENARIO_FILE_PATH + File.separator + scenario.getData();
		try (InputStream is = new FileInputStream(filename)) {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8));
			String line;
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				String attribute = dataline[0].trim().toLowerCase();
				scenarioFile.add(attribute);
			}
		} catch (IOException e) {
			e.printStackTrace();
			res.add("Error de apertura de fichero");
		}
		// Now, compare each row of both files to see if they match. They must be
		// EXACTLY the same. Also, catch exceptions in case of unparseable doubles or
		// rows with less than two columns
		int n = 0;
		String value = "";
		try (InputStream is = data.getInputStream()) {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8));
			String line = br.readLine(); // Skip the name in the country file
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				String attribute = dataline[0].trim().toLowerCase();
				String scenarioAttr = scenarioFile.get(n);
				n++;
				if (!attribute.equals(scenarioAttr)) {
					res.add("El atributo '" + attribute + "' en la línea " + (n + 1)
							+ " no coincide con el atributo '" + scenarioAttr
							+ "' del escenario");
				}
				if (!attribute.equals(Rules.COUNTRY_NAME.toLowerCase())
						&& !attribute.equals(Rules.COUNTRY_TYPE.toLowerCase())
						&& !attribute.equals(Rules.COUNTRY_SUBSCENARIO.toLowerCase())) {
					value = dataline[1];
					if (attribute.equals(Rules.NAVAL_POWER.toLowerCase())
							|| attribute.equals(Rules.DEVELOPED_INFRAESTRUCTURE.toLowerCase())) {
						if (!Rules.TRUE.contains(value.toLowerCase()) && !Rules.FALSE.contains(value.toLowerCase())) {
							res.add("No se reconoce el valor " + value + " del atributo "
									+ attribute + " en la línea " + (n + 1));
						}
					} else {
						Double.parseDouble(dataline[1]);
					}
				}
			}
			if (n < scenarioFile.size()) {
				res.add("El fichero del país no coincide en longitud con el del escenacio a partir de la línea " + (n+1) + ".");
			}
			if (line == null && n == 0) {
				res.add("El fichero estaba vacío");
			}
		} catch (IOException e) {
			e.printStackTrace();
			res.add("Error de apertura de fichero");
		} catch (IndexOutOfBoundsException ioobe) {
			ioobe.printStackTrace();
			if (n >= scenarioFile.size()) {
				res.add("Error de lectura de fichero: línea " + (n + 1)
						+ " no existe en el fichero del escenario");
			} else {
				res.add("Error de lectura de fichero: línea " + (n + 1)
						+ ", el fichero debe tener dos columnas separadas por ';'");
			}
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
			res.add("Error de lectura de fichero: no se reconoce el valor '" + value
					+ "' en la línea " + (n + 1));
		}
		return res;
	}

}
