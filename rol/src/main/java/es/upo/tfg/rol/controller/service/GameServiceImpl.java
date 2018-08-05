package es.upo.tfg.rol.controller.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.GameRepository;
import es.upo.tfg.rol.model.dao.TurnRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;
import es.upo.tfg.rol.model.pojos.comparators.GameByDateComparator;
import es.upo.tfg.rol.model.pojos.comparators.TurnByIdComparator;

@Service("gameService")
@Transactional
public class GameServiceImpl implements GameService {

	@Autowired
	private GameRepository gameRep;
	@Autowired
	private TurnRepository turnRep;

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
		List<Turn> turns = turnRep.findByGame(game);
		Turn currentTurn = game.getActiveTurn();
		Comparator<Turn> comp = new TurnByIdComparator();
		//Collections.sort(turns, comp);
		int index = turns.indexOf(currentTurn);
		// Secure against trampling the html
		if (index != (turns.size() - 1)) {
			Turn nextTurn = turns.get(index + 1);
			game.setActiveTurn(nextTurn);
		}		
		gameRep.save(game);
	}

}
