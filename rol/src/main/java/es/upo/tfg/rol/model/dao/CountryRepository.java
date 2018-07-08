package es.upo.tfg.rol.model.dao;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.Country;

public interface CountryRepository extends CrudRepository<Country, Long> {

}
