package es.upo.tfg.rol;

import java.util.HashMap;
import java.util.Map;

public class Rules {
	
	// Parsing constants
	public final static String COALITION_SEPARATOR = ";";
	public final static String INVOLVEMENT_SEPARATOR = ",";
	public final static String CLOSED_WAR = "closed";
	// Die constants
	public static final Map<Integer, Double> DIE;
	static {
		DIE = new HashMap<Integer, Double>();
		DIE.put(1, 0.0);
		DIE.put(2, 3.0);
		DIE.put(3, 7.0);
		DIE.put(4, 12.0);
		DIE.put(5, 17.0);
		DIE.put(6, 22.0);
	}
	public final static Integer DIE_SIDE = 6;
	// Rolls constants
	public static final Map<Integer, Double> ROLL_SCORE;
	static {
		ROLL_SCORE = new HashMap<Integer, Double>();
		ROLL_SCORE.put(1, 0.15);
		ROLL_SCORE.put(2, 0.25);
		ROLL_SCORE.put(3, 0.60);
	}
	public final static Integer MAX_ROLLS_PER_WAR = 3;

	public Rules() {
		// TODO Auto-generated constructor stub
	}

}
