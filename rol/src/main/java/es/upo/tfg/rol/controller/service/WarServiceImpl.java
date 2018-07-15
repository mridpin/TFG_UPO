package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.RollRepository;
import es.upo.tfg.rol.model.dao.WarRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

@Service("warService")
@Transactional
public class WarServiceImpl implements WarService {

	@Autowired
	private WarRepository warRep;
	@Autowired
	private RollRepository rollRep;

	@Override
	public War createWar(Game game) {
		War war = new War();
		return war;
	}

	@Override
	public void endWar(War war) {	
		// Only persist the war if it has rolls
		if (war.getId() != null) {
			war.setStatus("closed");
			warRep.save(war);
		}
	}

}
