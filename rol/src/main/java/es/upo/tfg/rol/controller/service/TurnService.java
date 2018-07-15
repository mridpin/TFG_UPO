package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.Game;

public interface TurnService {

	List<Turn> findTurns(Game game);

}
