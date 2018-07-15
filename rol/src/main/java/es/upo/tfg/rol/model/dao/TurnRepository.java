package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Turn;

public interface TurnRepository extends CrudRepository<Turn, Long> {

	List<Turn> findByGame(Game game);

}
