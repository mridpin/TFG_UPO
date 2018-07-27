package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.War;

public interface WarRepository extends CrudRepository<War, Long> {

	List<War> findByTurn(Turn t);

}
