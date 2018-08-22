package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

public interface RollService {
	
	List<Roll> findByWar(War war);

	Coalition getWinner(Roll lastRoll);
	
}
