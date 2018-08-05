package es.upo.tfg.rol.model.pojos.comparators;

import java.util.Comparator;

import es.upo.tfg.rol.model.pojos.Turn;

public class TurnByIdComparator implements Comparator<Turn> {

	public TurnByIdComparator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int compare(Turn o1, Turn o2) {
		return (int)(o1.getId() - o2.getId());
	}

}
