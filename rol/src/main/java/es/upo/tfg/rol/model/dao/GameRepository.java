package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface GameRepository extends CrudRepository<Game, Long> {

	/**
	 * Finds all the games where the user participated as a game master
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	public List<Game> findByMaster(User user);
	
	/**
	 * Finds all the games still open where the user participated as a game master
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	@Query(value = "FROM Game g where g.endDate is null and g.master = :user")
	public List<Game> findOpenByMaster(@Param("user") User user);
	
	/**
	 * Finds all the games already closed where the user participated as a game master
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	@Query(value = "FROM Game g where g.endDate is not null and g.master = :user")
	public List<Game> findClosedByMaster(@Param("user") User user);

	/**
	 * Finds all the games where the user participated as a player
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	@Query(value = "select g FROM Country c join c.player u join c.game g where u = :user")
	public List<Game> findByPlayer(@Param("user") User user);
	
	/**
	 * Finds all the games still open where the user participated as a player
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	@Query(value = "select g FROM Country c join c.player u join c.game g where g.endDate is null and u = :user")
	public List<Game> findOpenByPlayer(@Param("user") User user);
	
	/**
	 * Finds all the games already closed where the user participated as a player
	 * 
	 * @param user
	 *            the user in question
	 * @return list of games
	 */
	@Query(value = "select g FROM Country c join c.player u join c.game g where g.endDate is not null and u = :user")
	public List<Game> findClosedByPlayer(@Param("user") User user);
}
