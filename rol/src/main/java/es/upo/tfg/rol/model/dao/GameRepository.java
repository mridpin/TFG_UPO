package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface GameRepository extends CrudRepository<Game, Long>  {
	public List<Game> findByMaster(User user);
}
