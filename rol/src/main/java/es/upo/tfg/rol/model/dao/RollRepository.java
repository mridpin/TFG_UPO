package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

public interface RollRepository extends CrudRepository<Roll, Long> {

	List<Roll> findByWar(War war);

	@Query(value = "select r FROM Roll r join r.war w join w.turn t join t.game g where g = :game")
	List<Roll> findByGame(@Param("game") Game game);
	
	@Query(value="select distinct r FROM Coalition co, Involvement i, Country c, Roll r join r.war w join w.turn t join t.game g where g = :game and (r.attacker=co.id or r.defender=co.id) and co.id=i.coalition and i.country = :country")
	List<Roll> findByGameAndCountry(@Param("game") Game game, @Param("country") Country country);
	@Query(value="select distinct r FROM Coalition co, Involvement i, Country c, Roll r join r.war w join w.turn t join t.game g where g = :game and (r.attacker=co.id or r.defender=co.id) and co.id=i.coalition and i.country = :country and i.wonTheRoll=true")
	List<Roll> findWonRollsByGameAndCountry(@Param("game") Game game, @Param("country") Country country);

}
