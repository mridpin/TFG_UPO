package es.upo.tfg.rol.controller.service;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

public interface WarService {

	War createWar(Game game);

	void endWar(War war);

	Roll roll(Game game, War war, String name, Double attackerScore, Double defenderScore,
			String attackerCountries, String defenderCountries, String defenderName,
			String attackerName, String subscenario);

}
