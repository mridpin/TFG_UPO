package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.GameRepository;
import es.upo.tfg.rol.model.dao.UserRepository;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

@Service("gameService")
@Transactional
public class GameServiceImpl implements GameService {

	@Autowired
	private GameRepository gameRep;

	@Override
	public void saveGame(Game game) {
		gameRep.save(game);
	}
	

	@Override
	public List<Game> findMasteredGames(User user) {
		List<Game> masteredGames = gameRep.findByMaster(user);	
		return masteredGames;
	}

}
