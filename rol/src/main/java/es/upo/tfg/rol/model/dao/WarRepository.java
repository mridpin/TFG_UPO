package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.War;

public interface WarRepository extends CrudRepository<War, Long> {

	List<War> findByTurn(Turn t);

	@Query(value = "select w FROM War w join w.turn t join t.game g where g = :game")
	List<War> findByGame(@Param("game") Game game);

}
