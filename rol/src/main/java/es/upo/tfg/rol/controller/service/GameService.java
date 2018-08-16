package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;

public interface GameService {

	/**
	 * Persists a game in the database
	 * 
	 * @param game
	 *            to persist
	 */
	void saveGame(Game game);

	/**
	 * Creates a game with the data from the form and persists it
	 * 
	 * @param name
	 *            of the game
	 * @param master
	 *            of the game
	 * @param turns
	 *            of the game
	 * @param countries
	 *            in the game
	 * @param files
	 *            of the countries in the game
	 * @param scenario
	 *            of the game
	 * @return
	 */
	Game createGame(String name, User master, List<Turn> turns, List<Country> countries,
			List<MultipartFile> files, Scenario scenario);

	/**
	 * Finds all open (ie, unfinished) games from a user
	 * 
	 * @param user
	 *            to look games from
	 * @return a list of open games for this user. Can be empty
	 */
	List<Game> findOpenGames(User user);

	/**
	 * Finds a game by id
	 * 
	 * @param id
	 *            of the game
	 * @return a game with that id, or null if it doesn't exist
	 */
	Game findById(Long id);

	/**
	 * Closes a game by assigning an end date
	 * 
	 * @param game
	 *            to close
	 */
	void closeGame(Game game);

	/**
	 * Finds all closed (ie, finished) games from a user
	 * 
	 * @param user
	 *            to look games from
	 * @return a list of closed games for this user. Can be empty
	 */
	List<Game> findClosedGames(User user);

	/**
	 * Persists a turn
	 * 
	 * @param t
	 *            turn to persist
	 */
	void saveTurn(Turn t);

	/**
	 * Advance the turn in the game. Resets countries that haven't fought wars in
	 * this turn
	 * 
	 * @param game
	 *            to advance turn
	 */
	void nextTurn(Game game);

}
