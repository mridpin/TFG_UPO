package es.upo.tfg.rol.controller.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.CoalitionRepository;
import es.upo.tfg.rol.model.dao.CountryRepository;
import es.upo.tfg.rol.model.dao.InvolvementRepository;
import es.upo.tfg.rol.model.dao.RollRepository;
import es.upo.tfg.rol.model.dao.TurnRepository;
import es.upo.tfg.rol.model.dao.WarRepository;
import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Involvement;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.War;

@Service("warService")
@Transactional
public class WarServiceImpl implements WarService {

	public final static String COALITION_SEPARATOR = ";";
	public final static String INVOLVEMENT_SEPARATOR = ",";
	private static final Map<Integer, Double> DIE;
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
	
	@Autowired
	private WarRepository warRep;
	@Autowired
	private RollRepository rollRep;
	@Autowired
	private TurnRepository turnRep;
	@Autowired
	private CountryRepository countryRep;
	@Autowired
	private InvolvementRepository invRep;
	@Autowired
	private CoalitionRepository coalRep;

	@Override
	public War createWar(Game game) {
		War war = new War();
		return war;
	}

	@Override
	public void endWar(War war) {
		// Only persist the war if it has rolls
		if (war.getId() != null) {
			war.setStatus("closed");
			warRep.save(war);
		}
	}

	@Override
	public Roll roll(Game game, War war, String name, Double attackerScore,
			Double defenderScore, String attackerCountries, String defenderCountries,
			String defenderName, String attackerName, String subscenario) {
		// Get the list of turns from the game, then find the correct one from
		// subscenario
		List<Turn> turns = turnRep.findByGame(game);
		Turn turn = this.findTurnFromGame(turns, subscenario);
		if (turn == null) {
			return null;
		}
		// Assign turn to war
		war.setTurn(turn);
		// If this is the first roll of the war, save it
		if (war.getId() == null) {
			// TODO: Get the # war of this turn
			war.setName("".equals(name) ? "Guerra" : name);
			warRep.save(war);
		}
		// Create the coalitions
		List<Country> countries = countryRep.findByGame(game);
		Coalition attacker = new Coalition();
		attacker.setName("".equals(attackerName) ? "Coalición Atacante" : attackerName);
		Coalition defender = new Coalition();
		defender.setName("".equals(defenderName) ? "Coalición Defensora" : defenderName);
		coalRep.save(attacker);
		coalRep.save(defender);
		// Assemble the involvements with the country strings and commitment percents
		String[] attackers = attackerCountries.split(COALITION_SEPARATOR);
		String[] defenders = defenderCountries.split(COALITION_SEPARATOR);
		List<Involvement> attackerInvolvements = new ArrayList<>();
		List<Involvement> defenderInvolvements = new ArrayList<>();
		try {
			for (String s : attackers) {
				// Assemble the interventions
				String[] c = s.split(INVOLVEMENT_SEPARATOR);
				Involvement inv = new Involvement();
				inv.setCoalition(attacker);
				// TODO: ESTO DA FALLO
				inv.setCountry(this.findCountryByName(c[0], countries));
				inv.setInvolvementPercent(Double.parseDouble(c[1]));
				attackerInvolvements.add(inv);
				invRep.save(inv);
			}
			for (String s : defenders) {
				// Assemble the interventions
				String[] c = s.split(INVOLVEMENT_SEPARATOR);
				Involvement inv = new Involvement();
				inv.setCoalition(defender);
				inv.setCountry(this.findCountryByName(c[0], countries));
				inv.setInvolvementPercent(Double.parseDouble(c[1]));
				defenderInvolvements.add(inv);
				invRep.save(inv);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		// Roll the dice and resolve the scores
		Random rand = new Random();
		Integer attackerRoll = rand.nextInt(DIE_SIDE) + 1;
		Integer defenderRoll = rand.nextInt(DIE_SIDE) + 1;
		Double attackerRollScore = DIE.get(attackerRoll);
		Double defenderRollScore = DIE.get(attackerRoll);
		attackerScore += attackerRollScore;
		defenderScore += defenderRollScore;
		// Assign the winner and loser
		Roll roll = new Roll();
		roll.setAttacker(attacker);
		roll.setDefender(defender);
		roll.setAttackerScore(attackerScore);
		roll.setDefenderScore(defenderScore);
		roll.setWar(war);
		rollRep.save(roll);
		// Return the new roll
		return roll;
	}

	private Country findCountryByName(String name, List<Country> countries) {
		for (Country c : countries) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	private Turn findTurnFromGame(List<Turn> turns, String subscenario) {
		for (Turn turn : turns) {
			if (turn.getSubscenario().equals(subscenario)) {
				return turn;
			}
		}
		return null;
	}
}
