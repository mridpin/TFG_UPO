package es.upo.tfg.rol.controller.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.WarRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.War;

@Service("warService")
@Transactional
public class WarServiceImpl implements WarService {

	@Autowired
	private WarRepository wRep;

	@Override
	public War createWar(Game game) {
		War war = new War();
		return war;
	}

}
