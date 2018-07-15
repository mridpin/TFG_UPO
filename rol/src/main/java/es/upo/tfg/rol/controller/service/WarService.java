package es.upo.tfg.rol.controller.service;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.War;

public interface WarService {

	War createWar(Game game);

	void endWar(War war);

}
