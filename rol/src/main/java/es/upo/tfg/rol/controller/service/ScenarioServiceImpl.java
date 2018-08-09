package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.fabric.xmlrpc.base.Array;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.model.dao.ScenarioRepository;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

@Service("scenarioService")
@Transactional
public class ScenarioServiceImpl implements ScenarioService {

	@Autowired
	private ScenarioRepository scRep;

	public ScenarioServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Scenario createScenario(String name, String description, MultipartFile data, User user) {
		// TODO: Raise exceptions with non valid csvs or non utf8 ones
		Scenario scenario = new Scenario();
		scenario.setName(name);
		scenario.setDescription(description);
		scenario.setAuthor(user);
		String millis = "" + System.currentTimeMillis();
		String filename = millis + "-" + name + ".csv";
		// Prepare and store datafile
		Path dataPath = Paths.get(Rules.SCENARIO_FILE_PATH);
		try (InputStream inputStream = data.getInputStream()) {
			Files.copy(inputStream, dataPath.resolve(filename),
					StandardCopyOption.REPLACE_EXISTING);
			scenario.setData(filename);
			scRep.save(scenario);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scenario;
	}

	@Override
	public Map<String, Map<String, Map<String, Double>>> mapScenario(Scenario scenario) {
		String line = null;
		String subscenario;
		String type;
		Map<String, Map<String, Map<String, Double>>> attributes = new HashMap<>();
		Map<String, Map<String, Double>> subscenarioAttributes = new HashMap<>();
		Map<String, Double> typeAttributes = new HashMap<>();
		String filename = Rules.SCENARIO_FILE_PATH + File.separator + scenario.getData();
		try (InputStream is = new FileInputStream(filename)){			
			BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				String whatDo = dataline[0].trim().toLowerCase();
				switch (whatDo) {
				case "nombre":
					break;
				case "subescenario":
					subscenario = dataline[1];
					subscenarioAttributes = new HashMap<>();
					attributes.put(subscenario, subscenarioAttributes);
					break;
				case "tipo":
					type = dataline[1];
					typeAttributes = new HashMap<>();
					subscenarioAttributes.put(type, typeAttributes);
					break;
				default:
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
			return null;
		} catch (NumberFormatException e) {
			System.err.print(scenario.getName() + "\n");
			e.printStackTrace();
			return null;
		}
		return attributes;
	}

	@Override
	public List<Scenario> findAllScenarios() {
		List<Scenario> scenarios = new ArrayList<>();
		scRep.findAll().forEach(scenarios::add);
		return scenarios;
	}

	@Override
	public Scenario findById(Long id) {
		Optional<Scenario> scenarioOp = scRep.findById(id);
		return (scenarioOp.isPresent()) ? scenarioOp.get() : null;
	}

}
