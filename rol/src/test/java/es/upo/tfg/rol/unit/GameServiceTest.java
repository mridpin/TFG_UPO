package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.assertj.core.util.Arrays;
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
import es.upo.tfg.rol.controller.service.GameService;
import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GameServiceTest {

	@Autowired
	GameService gs;
	
	@Autowired
	UserService us;
	
	@Autowired
	ScenarioService ss;
	
	public GameServiceTest() {
		// TODO Auto-generated constructor stub
	}
	
	public User user;
	public Scenario scenario;
	
	@Before
	public void createUser() {
		String nickname = TestUtils.createRandomString();
		User user = new User("avatar", "name", nickname, "password");
		us.saveUser(user);
		this.user = user;
	}
	
	@Before
	public void createScenario() {
		Scenario scenario = new Scenario(null, "scenario", "description", TestUtils.TEST_SCENARIO_FILE_PATH, null, user);
		ss.save(scenario);
		this.scenario = scenario;
	}
	
	@Test
	public void createGameTest_ok() {
		this.createUser();
		this.createScenario();
		Country c = new Country(null, "Alemania", TestUtils.TEST_COUNTRY_FILE_PATH, user, null);
		List<Country> countries = new ArrayList<>();
		countries.add(c);
		File file = new File(Rules.STATIC_FILE_PATH + File.separator + "Alemania.csv");
	    InputStream input;
	    MultipartFile multipartFile = null;
		try {
			input = new FileInputStream(file);
		    multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<MultipartFile> files = new ArrayList<>();
		files.add(multipartFile);
		List<Turn> turns = new ArrayList<>();
		Turn turn1 = new Turn("1860", null);
		Turn turn2 = new Turn("1870", null);
		turns.add(turn1);
		turns.add(turn2);

		Game newGame = gs.createGame("game", user, turns, countries, files, scenario);		
		Game game = new Game(newGame.getId(), "game", scenario, newGame.getStartDate(), null, user, turn1);
		
		assertEquals(game, newGame);		
	}
	
//	@Test
//	public void findOpenGames_closeGame_findCloseGames_ok() {
//		// TODO: DO THIS
//	}
	
	@Test
	public void nextTurn_ok() {
		this.createUser();
		this.createScenario();
		Country c = new Country(null, "Alemania", TestUtils.TEST_COUNTRY_FILE_PATH, user, null);
		List<Country> countries = new ArrayList<>();
		countries.add(c);
		File file = new File(Rules.STATIC_FILE_PATH + File.separator + "Alemania.csv");
	    InputStream input;
	    MultipartFile multipartFile = null;
		try {
			input = new FileInputStream(file);
		    multipartFile = new MockMultipartFile("file",
		            file.getName(), "text/plain", IOUtils.toByteArray(input));			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<MultipartFile> files = new ArrayList<>();
		files.add(multipartFile);
		List<Turn> turns = new ArrayList<>();
		Turn turn1 = new Turn("1860", null);
		Turn turn2 = new Turn("1870", null);
		turns.add(turn1);
		turns.add(turn2);
		
		Game newGame = gs.createGame("game", user, turns, countries, files, scenario);	
		gs.nextTurn(newGame);
		
		assertEquals(turn2, newGame.getActiveTurn());
	}

}
