package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Involvement;

public interface InvolvementRepository extends CrudRepository<Involvement, Long> {

	List<Involvement> findByCoalition(Coalition attacker);

}
