package es.upo.tfg.rol.controller.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.internal.Path;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.model.dao.GameRepository;
import es.upo.tfg.rol.model.dao.InvolvementRepository;
import es.upo.tfg.rol.model.dao.TurnRepository;
import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Involvement;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;
import es.upo.tfg.rol.model.pojos.War;
import es.upo.tfg.rol.model.pojos.comparators.GameByDateComparator;
import es.upo.tfg.rol.model.pojos.comparators.TurnByIdComparator;

@Service("gameService")
@Transactional
public class GameServiceImpl implements GameService {

	@Autowired
	private GameRepository gameRep;
	@Autowired
	private TurnRepository turnRep;
	@Autowired
	private InvolvementRepository invRep;

	@Autowired
	private CountryService countryService;
	@Autowired
	private WarService warService;
	@Autowired
	private RollService rollService;

	@Override
	public void saveGame(Game game) {
		gameRep.save(game);
	}

	@Override
	public List<Game> findOpenGames(User user) {
		List<Game> masteredOpenGames = gameRep.findOpenByMaster(user);
		List<Game> playedOpenGames = gameRep.findOpenByPlayer(user);
		List<Game> gamesParticipated = new ArrayList<>();
		gamesParticipated.addAll(masteredOpenGames);
		gamesParticipated.addAll(playedOpenGames);
		Comparator<Game> c = new GameByDateComparator();
		Collections.sort(gamesParticipated, c);
		return gamesParticipated;
	}

	@Override
	public Game findById(Long id) {
		Optional<Game> game = gameRep.findById(id);
		return (game.isPresent()) ? game.get() : null;
	}

	@Override
	public void closeGame(Game game) {
		game.setEndDate(new Date());
		gameRep.save(game);
	}

	@Override
	public List<Game> findClosedGames(User user) {
		List<Game> masteredClosedGames = gameRep.findClosedByMaster(user);
		List<Game> playedClosedGames = gameRep.findClosedByPlayer(user);
		List<Game> gamesParticipated = new ArrayList<>();
		gamesParticipated.addAll(masteredClosedGames);
		gamesParticipated.addAll(playedClosedGames);
		Comparator<Game> c = new GameByDateComparator();
		Collections.sort(gamesParticipated, c);
		return gamesParticipated;
	}

	@Override
	public void saveTurn(Turn t) {
		turnRep.save(t);
	}

	@Override
	public void nextTurn(Game game) {
		// Assign new turn to game
		List<Turn> turns = turnRep.findByGame(game);
		Turn currentTurn = game.getActiveTurn();
		Comparator<Turn> comp = new TurnByIdComparator();
		// Collections.sort(turns, comp);
		int index = turns.indexOf(currentTurn);
		// Secure against trampling the html
		if (index != (turns.size() - 1)) {
			Turn nextTurn = turns.get(index + 1);
			game.setActiveTurn(nextTurn);
		}
		gameRep.save(game);
		// Restore countries that haven't fought wars this turn
		this.restoreCountries(game, currentTurn);
	}

	private void restoreCountries(Game game, Turn turn) {
		List<Country> countries = countryService.findCountries(game);
		Set<Country> warringCountries = new HashSet<>();
		List<War> turnWars = warService.findByTurn(turn);
		// Iterate all the rolls and add to a list all the warring countries
		for (War w : turnWars) {
			List<Roll> rolls = rollService.findByWar(w);
			for (Roll r : rolls) {
				Coalition attacker = r.getAttacker();
				Coalition defender = r.getDefender();
				List<Country> attackers = this.countriesInCoalition(attacker);
				List<Country> defenders = this.countriesInCoalition(defender);
				warringCountries.addAll(attackers);
				warringCountries.addAll(defenders);
			}
		}
		// Perform minus operation
		countries.removeAll(warringCountries);
		for (Country c : countries) {
			this.resetCountry(c);
		}

	}

	private void resetCountry(Country c) {
		String ogFilename = Rules.COUNTRY_FILE_PATH + File.separator + c.getData()
				+ Rules.ORIGINAL_FILE;
		String currFilename = Rules.COUNTRY_FILE_PATH + File.separator + c.getData();
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES };
		try {
			Files.deleteIfExists(Paths.get(currFilename));
			Files.copy(Paths.get(ogFilename), Paths.get(currFilename), options);
		} catch (IOException e) {
			// TODO: HANDLE THIS EXCEPTION? PRIORITY = 3
			e.printStackTrace();
		}
	}

	private List<Country> countriesInCoalition(Coalition coalition) {
		List<Country> result = new ArrayList<>();
		List<Involvement> attackers = invRep.findByCoalition(coalition);
		for (Involvement i : attackers) {
			result.add(i.getCountry());
		}
		return result;
	}

}
