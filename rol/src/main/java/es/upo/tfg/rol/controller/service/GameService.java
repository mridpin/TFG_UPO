package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface GameService {

	void saveGame(Game game);

	List<Game> findMasteredGames(User user);

}
