package es.upo.tfg.rol;

import java.util.HashMap;
import java.util.Map;

public class Rules {

	// Parsing constants
	public final static String COALITION_SEPARATOR = ";";
	public final static String INVOLVEMENT_SEPARATOR = ",";
	public final static String CLOSED_WAR = "closed";
	public final static String ORIGINAL_FILE = "_original";
	public final static String COUNTRY_FILE_PATH = "countryData";
	public final static String SCENARIO_FILE_PATH = "scenarios";
	public final static String COUNTRY_NAME = "Nombre";
	public final static String COUNTRY_SUBSCENARIO = "Subescenario";
	public final static String COUNTRY_TYPE = "Tipo";
	public final static String DEVELOPED_INFRAESTRUCTURE = "Infraestructura desarrollada";
	public final static String NAVAL_POWER = "Potencia naval";
	public final static String SEMICOLON = ";";
	// Combat constants
	public final static Double BLOCKADE_PENALTY = 0.75;
	public final static Double INLAND_BATTLE_PENALTY = 0.75;
	public final static Double DEVELOPED_INFRAESTRUCTURE_ROLL = 3.0;
	public final static Double INFANTRY_ATTACK_PENALTY = 1 - 0.10;
	public final static Double INFANTRY_DEFENSE_BONUS = 1 + 0.07;
	// Attribute constants
	// Keywords to look for to detect which attributes or types of attributes must be modified
	public final static String[] INFANTRY_ATTR_KEYWORDS = { "infanteria", "infantería",
			"soldados", "soldado", "reserva", "reservas", "reservistas", "caballeria", "caballería", "caballeros"};
	public final static String[] RESERVES_ATTR_KEYWORDS = {"reserva", "reservas", "reservistas", "reservista"};
	public final static String[] NAVAL_TYPE_KEYWORDS = {"navales", "naval", "marina", "armada"};
	public final static String[] ECONOMY_TYPE_KEYWORDS = {"economía", "economia", "económicos", "económico", "economicos", "economico"};
	public final static String[] MILITARY_TYPE_KEYWORDS = {"militar", "militares"};
	// Used to tag relevant attributes and types so that the client can identify them and apply the logic
	public final static String ECONOMY = "ECONOMY";
	public final static String NAVAL = "NAVAL";
	public final static String MILITARY = "MILITARY";
	public final static String INFANTRY = "INFANTRY";
	public final static String RESERVES = "RESERVES";
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
	public final static Integer HIGHEST_ROLL = 6;
	public final static Integer LOWEST_ROLL = 1;
	// Rolls constants
	public static final Map<Integer, Double> ROLL_SCORE;
	static {
		ROLL_SCORE = new HashMap<Integer, Double>();
		ROLL_SCORE.put(1, 0.15);
		ROLL_SCORE.put(2, 0.25);
		ROLL_SCORE.put(3, 0.60);
	}
	public final static Integer MAX_ROLLS_PER_WAR = 3;
	public final static Double HIGHEST_ROLL_MODIFIER = 1.05;
	public final static Double LOWEST_ROLL_MODIFIER = 0.95;
	public final static Double COST_OF_WAR_LOSER = 0.05;
	public final static Double COST_OF_WAR_WINNER = 0.02;

	public Rules() {
		// TODO Auto-generated constructor stub
	}

	public static Map<String, Object> getRules() {
		Map<String, Object> rules = new HashMap<>();
		// Add the numeric rules
		rules.put("BLOCKADE_PENALTY", Rules.BLOCKADE_PENALTY);
		rules.put("INLAND_BATTLE_PENALTY", Rules.INLAND_BATTLE_PENALTY);
		rules.put("DEVELOPED_INFRAESTRUCTURE", Rules.DEVELOPED_INFRAESTRUCTURE);
		rules.put("DEVELOPED_INFRAESTRUCTURE_ROLL", Rules.DEVELOPED_INFRAESTRUCTURE_ROLL);
		rules.put("NAVAL_POWER", Rules.NAVAL_POWER);
		rules.put("INFANTRY_ATTACK_PENALTY", Rules.INFANTRY_ATTACK_PENALTY);
		rules.put("INFANTRY_DEFENSE_BONUS", Rules.INFANTRY_DEFENSE_BONUS);
		// Add the keywords
		rules.put("INFANTRY_ATTR_KEYWORDS", Rules.INFANTRY_ATTR_KEYWORDS);
		rules.put("RESERVES_ATTR_KEYWORDS", Rules.RESERVES_ATTR_KEYWORDS);
		rules.put("NAVAL_TYPE_KEYWORDS", Rules.NAVAL_TYPE_KEYWORDS);
		rules.put("ECONOMY_TYPE_KEYWORDS", Rules.ECONOMY_TYPE_KEYWORDS);
		rules.put("MILITARY_TYPE_KEYWORDS", Rules.MILITARY_TYPE_KEYWORDS);
		return rules;
	}

}
