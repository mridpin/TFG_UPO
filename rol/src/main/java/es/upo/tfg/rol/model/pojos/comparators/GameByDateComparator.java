package es.upo.tfg.rol.model.pojos.comparators;

import java.util.Comparator;

import es.upo.tfg.rol.model.pojos.Game;

public class GameByDateComparator implements Comparator<Game> {

	public GameByDateComparator() {
		
	}

	@Override
	public int compare(Game g1, Game g2) {
		return g1.getStartDate().compareTo(g2.getStartDate());
	}

}
