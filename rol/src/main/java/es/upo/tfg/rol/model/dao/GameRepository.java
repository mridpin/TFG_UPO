package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface GameRepository extends CrudRepository<Game, Long>  {
	
	public List<Game> findByMaster(User user);

	@Query(value = "SELECT Game FROM Country c " + 
			"JOIN c.player u " + 
			"JOIN c.game g " +
			"WHERE u=user AND " +
			"g.endDate is not null")
	public List<Game> findByPlayer(@Param("user") User user);
}
