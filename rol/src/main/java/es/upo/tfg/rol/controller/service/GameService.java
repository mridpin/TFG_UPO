package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;

public interface GameService {

	void saveGame(Game game);

	List<Game> findOpenGames(User user);
	
	Game findById(Long id);

	void closeGame(Game game);

	List<Game> findClosedGames(User user);

	void saveTurn(Turn t);

}
