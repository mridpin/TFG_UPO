package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Scenario;

public interface TurnService {

	List<Turn> findTurns(Game game);

	/**
	 * Searches for the subscenario keyword in a scenario file to generate valid
	 * turns
	 * 
	 * @param scenario
	 *            we want to create turns for
	 * @param game
	 *            to assign to turns
	 * @return a list of turns
	 */
	List<Turn> generateTurns(Scenario scenario, Game game);

	List<Turn> findByGame(Game game);

	/**
	 * Finds a turn from a game by name
	 * 
	 * @param turns
	 *            of the game
	 * @param subscenario
	 *            name of the turn to look for
	 * @return the Turn if it exists, null if it doesn't
	 */
	Turn findTurnFromGame(List<Turn> turns, String subscenario);
}
