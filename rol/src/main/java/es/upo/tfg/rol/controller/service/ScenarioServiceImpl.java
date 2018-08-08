package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

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
		String filename = millis + "-" + name;
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

}
