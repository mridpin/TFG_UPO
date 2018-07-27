package es.upo.tfg.rol.controller.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.RollRepository;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.War;

@Service("rollService")
@Transactional
public class RollServiceImpl implements RollService {

	@Autowired
	private RollRepository rollRep;
	
	public RollServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<Roll> findByWar(War war) {
		return rollRep.findByWar(war);
	}

}
