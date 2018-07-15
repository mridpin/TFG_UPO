package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

public interface RollRepository extends CrudRepository<Roll, Long> {

	List<Roll> findByWar(War war);
	
}
