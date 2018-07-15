package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.TurnRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Turn;

@Service("turnService")
@Transactional
public class TurnServiceImpl implements TurnService {

	@Autowired
	TurnRepository turnRep;
	
	public TurnServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Turn> findTurns(Game game) {
		return turnRep.findByGame(game);
	}

}
