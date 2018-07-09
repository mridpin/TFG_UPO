package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;

public interface CountryRepository extends CrudRepository<Country, Long> {

	public List<Country> findByGame(Game game);

}
