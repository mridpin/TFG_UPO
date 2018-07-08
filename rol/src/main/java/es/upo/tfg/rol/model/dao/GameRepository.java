package es.upo.tfg.rol.model.dao;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Game;

public interface GameRepository extends CrudRepository<Game, Long>  {

}
