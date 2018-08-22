package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.TestUtils;
import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ScenarioServiceTest {

	@Autowired
	UserService us;
	@Autowired
	ScenarioService ss;

	public User user;

	@Before
	public void createUser() {
		String nickname = TestUtils.createRandomString();
		User user = new User("avatar", "name", nickname, "password");
		us.saveUser(user);
		this.user = user;
	}

	@Test
	public void createScenarioTest_ok_mapScenarioTest_ok() {
		Scenario scenario = new Scenario(null, "scenario", "description",
				TestUtils.TEST_SCENARIO_FILE_PATH, null, user);
		try {
			File file = new File(TestUtils.TEST_SCENARIO_FILE_PATH);
			InputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile("file", file.getName(),
					"text/plain", IOUtils.toByteArray(input));
			Scenario newScenario = ss.createScenario("scenario", "description", multipartFile,
					user);
			scenario.setId(newScenario.getId());
			newScenario.setData(TestUtils.TEST_SCENARIO_FILE_PATH); // overwrites the procedurally generated file name
			assertEquals(scenario, newScenario);
			
			Map<String, Map<String, Map<String, Double>>> map = this.generateScenario();
			Map<String, Map<String, Map<String, Double>>> newMap = ss.mapScenario(scenario);
			
			assertEquals(map, newMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validateScenarioFile_ok() {
		try {
			File file = new File(TestUtils.TEST_SCENARIO_FILE_PATH);
			InputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile("file", file.getName(),
					"text/plain", IOUtils.toByteArray(input));

			assertEquals(ss.validateScenarioFile(multipartFile).size(), 0);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Test
	public void validateScenarioFile_2Errors() {
		try {
			File file = new File(TestUtils.TEST_SCENARIOERRORS_FILE_PATH);
			InputStream input = new FileInputStream(file);
			MultipartFile multipartFile = new MockMultipartFile("file", file.getName(),
					"text/plain", IOUtils.toByteArray(input));

			assertEquals(ss.validateScenarioFile(multipartFile).size(), 2);
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	private Map<String, Map<String, Map<String, Double>>> generateScenario() {
		Map<String, Map<String, Map<String, Double>>> map = new HashMap<>();
		Map<String, Map<String, Double>> sub1860Attributes = new HashMap<>();
		Map<String, Map<String, Double>> sub1870Attributes = new HashMap<>();
		Map<String, Double> eco1860Attributes = new HashMap<>();
		Map<String, Double> eco1870Attributes = new HashMap<>();
		Map<String, Double> mil1860Attributes = new HashMap<>();
		Map<String, Double> mil1870Attributes = new HashMap<>();
		eco1860Attributes.put("PIB", 10.0);
		eco1870Attributes.put("PIB", 10.0);
		mil1860Attributes.put("Infraestructura desarrollada", 1.0);
		mil1860Attributes.put("Soldados", 8.0);
		mil1870Attributes.put("Infraestructura desarrollada", 1.0);
		mil1870Attributes.put("Soldados", 8.0);
		sub1860Attributes.put("economicos", eco1860Attributes);
		sub1860Attributes.put("militares", mil1860Attributes);
		sub1870Attributes.put("militares", mil1870Attributes);
		sub1870Attributes.put("economicos", eco1870Attributes);
		map.put("1860", sub1860Attributes);
		map.put("1870", sub1870Attributes);
		return map;
	}
}
