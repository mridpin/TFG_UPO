package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.model.dao.TurnRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;

@Service("turnService")
@Transactional
public class TurnServiceImpl implements TurnService {

	@Autowired
	TurnRepository turnRep;

	public TurnServiceImpl() {
	}

	@Override
	public List<Turn> findTurns(Game game) {
		return turnRep.findByGame(game);
	}

	@Override
	public List<Turn> generateTurns(Scenario scenario, Game game) {
		List<Turn> result = new ArrayList<>();
		String line = null;
		String subscenario;
		String filename = Rules.SCENARIO_FILE_PATH + File.separator + scenario.getData();
		try (InputStream is = new FileInputStream(filename)) {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(is, StandardCharsets.UTF_8));
			while ((line = br.readLine()) != null) {
				String[] dataline = line.split(";");
				if (Rules.COUNTRY_SUBSCENARIO.equals(dataline[0].trim())) {
					subscenario = dataline[1].trim();
					Turn turn = new Turn();
					turn.setSubscenario(subscenario);
					turn.setGame(game);
					result.add(turn);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

}
