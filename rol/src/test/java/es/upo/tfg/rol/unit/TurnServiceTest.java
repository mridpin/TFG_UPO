package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.upo.tfg.rol.TestUtils;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TurnServiceTest {

	@Autowired
	TurnService ts;

	@Test
	public void generateTurnsTest_ok() {
		Game game = new Game(1L, "name", null, new Date(), null, null, null);
		Scenario scenario = new Scenario(1L, "scenario", null, TestUtils.TEST_SCENARIO_FILE_PATH, null, null);
		assertEquals(ts.generateTurns(scenario, game).size(), 2);		
	}
}
