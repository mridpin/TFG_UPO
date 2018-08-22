package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.TestUtils;
import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CountryServiceTest {

	@Autowired
	CountryService cs;
	@Autowired
	UserService us;
		
	public User user;
	
	@Before
	public void createUser() {
		String nickname = TestUtils.createRandomString();
		User user = new User("avatar", "name", nickname, "password");
		us.saveUser(user);
		this.user = user;
	}
	
	@Test
	public void assembleCountryTest_ok() {
		try {
		File file = new File(Rules.STATIC_FILE_PATH + File.separator + "Alemania.csv");
	    InputStream input = new FileInputStream(file);
	    MultipartFile multipartFile = new MockMultipartFile("file",
	            file.getName(), "text/plain", IOUtils.toByteArray(input));
	    Country country = new Country(null, "Alemania", Rules.STATIC_FILE_PATH + File.separator + "Alemania.csv", user, null);
	    assertEquals(country, cs.assembleCountry(user, multipartFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void mapCountryTest_ok() {
		Map<String, Map<String, Map<String, Double>>> map = this.generateMap();
		Country country = new Country(null, "Alemania", TestUtils.TEST_COUNTRY_FILE_PATH, user, null);
		Map<String, Map<String, Map<String, Double>>> newMap = cs.mapCountry(country);
		
		assertEquals(map, newMap);
	}
	
	@Test
	public void validateCountryFile_ok() {
		Scenario scenario = new Scenario(null, "18thCent", "description", Rules.STATIC_FILE_PATH + File.separator + "TestScenario.csv", null, user);
		
		try {
			File file = new File(TestUtils.TEST_COUNTRY_FILE_PATH);
		    InputStream input = new FileInputStream(file);
		    MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
		    List<String> errors = cs.validateCountryFile(scenario, multipartFile);
		    assertEquals(0, errors.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@Test
	public void validateCountryFile_2Errors() {
		Scenario scenario = new Scenario(null, "18thCent", "description", TestUtils.TEST_SCENARIO_FILE_PATH, null, user);
		
		try {
			File file = new File(TestUtils.TEST_COUNTRYERROR_FILE_PATH);
		    InputStream input = new FileInputStream(file);
		    MultipartFile multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));
		    List<String> errors = cs.validateCountryFile(scenario, multipartFile);
		    assertEquals(2, errors.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	@Test
	public void demapCountryTest_ok() {
		Map<String, Map<String, Map<String, Double>>> map = this.generateMap();
		Country country = new Country(null, "Alemania", TestUtils.TEST_DEMAPCOUNTRY_FILE_PATH, user, null);
		cs.demapCountry(map, country);
		Map<String, Map<String, Map<String, Double>>> newMap = cs.mapCountry(country);
		assertEquals(map, newMap);
	}
	
	private Map<String, Map<String, Map<String, Double>>> generateMap() {
		Map<String, Map<String, Map<String, Double>>> map = new HashMap<>();
		Map<String, Map<String, Double>> sub1860Attributes = new HashMap<>();
		Map<String, Map<String, Double>> sub1870Attributes = new HashMap<>();
		Map<String, Double> eco1860Attributes = new HashMap<>();
		Map<String, Double> eco1870Attributes = new HashMap<>();
		Map<String, Double> mil1860Attributes = new HashMap<>();
		Map<String, Double> mil1870Attributes = new HashMap<>();
		eco1860Attributes.put("PIB", 1000.0);
		eco1870Attributes.put("PIB", 2000.0);
		mil1860Attributes.put("Infraestructura desarrollada", 1.0);
		mil1860Attributes.put("Soldados", 1000.0);
		mil1870Attributes.put("Infraestructura desarrollada", 1.0);
		mil1870Attributes.put("Soldados", 2000.0);
		sub1860Attributes.put("economicos", eco1860Attributes);
		sub1860Attributes.put("militares", mil1860Attributes);
		sub1870Attributes.put("militares", mil1870Attributes);
		sub1870Attributes.put("economicos", eco1870Attributes);
		map.put("1860", sub1860Attributes);
		map.put("1870", sub1870Attributes);
		return map;
	}

}
