package es.upo.tfg.rol.controller.service;

import java.util.List;
import java.util.Map;

import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.War;

public interface WarService {

	War createWar(Game game);

	void endWar(War war);

	List<War> findByGame(Game game);

	War findById(String warId);

	War findOpenWar(Game game);

	List<War> findByTurn(Turn t);

	Roll roll(Game game, War war, String name, Double attackerScore, Double defenderScore,
			String attackerCountries, String defenderCountries, String defenderName,
			String attackerName, Turn turn);
	
	/**
	 * Deep copies a country map
	 * @param map to copy
	 * @return copied map
	 */
	Map<String, Map<String, Map<String, Double>>> copyMap(
			Map<String, Map<String, Map<String, Double>>> map);

	Coalition findWinner(War war);
	
}
