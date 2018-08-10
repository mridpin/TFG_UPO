package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;

public interface GameService {

	void saveGame(Game game);

	Game createGame(String name, User master, List<Turn> turns, List<Country> countries,
			List<MultipartFile> files, Scenario scenario);

	List<Game> findOpenGames(User user);

	Game findById(Long id);

	void closeGame(Game game);

	List<Game> findClosedGames(User user);

	void saveTurn(Turn t);

	void nextTurn(Game game);

}
