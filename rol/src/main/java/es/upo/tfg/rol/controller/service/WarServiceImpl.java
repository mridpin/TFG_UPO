package es.upo.tfg.rol.controller.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.Rules;
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
	
	@Autowired
	private CountryService countryServ;

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
		// Get the list of turns from the game, then find the correct one from the
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
			if ("".equals(name)) {
				// TODO: UPDATE DESIGN DIAGRAM TO REFLECT THESE CHANGES
				int warNumber = warRep.findByTurn(turn).size();
				String genericWarName = "Guerra " + warNumber + " de "
						+ turn.getSubscenario();
				war.setName(genericWarName);
			} else {
				war.setName(name);
			}
			warRep.save(war);
		} else {
			// TODO: UPDATE DESIGN DIAGRAM TO REFLECT THESE CHANGES
			List<Roll> rolls = rollRep.findByWar(war);
			attackerName = rolls.get(rolls.size() - 1).getAttacker().getName();
			defenderName = rolls.get(rolls.size() - 1).getAttacker().getName();
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
		String[] attackers = attackerCountries.split(Rules.COALITION_SEPARATOR);
		String[] defenders = defenderCountries.split(Rules.COALITION_SEPARATOR);
		List<Involvement> attackerInvolvements = new ArrayList<>();
		List<Involvement> defenderInvolvements = new ArrayList<>();
		try {
			for (String s : attackers) {
				// Assemble the interventions
				String[] c = s.split(Rules.INVOLVEMENT_SEPARATOR);
				Involvement inv = new Involvement();
				inv.setCoalition(attacker);
				inv.setCountry(this.findCountryByName(c[0], countries));
				inv.setInvolvementPercent(Double.parseDouble(c[1]));
				attackerInvolvements.add(inv);
				invRep.save(inv);
			}
			for (String s : defenders) {
				// Assemble the interventions
				String[] c = s.split(Rules.INVOLVEMENT_SEPARATOR);
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
		Integer nRoll = rollRep.findByWar(war).size() + 1;
		Random rand = new Random();
		Integer attackerRoll = rand.nextInt(Rules.DIE_SIDE) + 1;
		Integer defenderRoll = rand.nextInt(Rules.DIE_SIDE) + 1;
		Double attackerRollScore = Rules.DIE.get(attackerRoll);
		Double defenderRollScore = Rules.DIE.get(defenderRoll);
		attackerScore = (attackerScore + attackerRollScore) * Rules.ROLL_SCORE.get(nRoll);
		if (attackerRoll.doubleValue() == Rules.HIGHEST_ROLL) {
			attackerRollScore *= Rules.HIGHEST_ROLL_MODIFIER;
		}
		if (attackerRoll.doubleValue() == Rules.LOWEST_ROLL) {
			attackerRollScore *= Rules.LOWEST_ROLL_MODIFIER;
		}
		defenderScore = (defenderScore + defenderRollScore) * Rules.ROLL_SCORE.get(nRoll);
		if (defenderRoll.doubleValue() == Rules.HIGHEST_ROLL) {
			defenderScore *= Rules.HIGHEST_ROLL_MODIFIER;
		}
		if (defenderRoll.doubleValue() == Rules.LOWEST_ROLL) {
			defenderScore *= Rules.LOWEST_ROLL_MODIFIER;
		}
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

	private void applyCostOfWar(Roll roll) {
		// Separate countries into winners and losers
		List<Involvement> winners;
		List<Involvement> losers;
		if (roll.getAttackerScore() > roll.getDefenderScore()) {
			winners = roll.getAttacker().getInvolvements();
			losers = roll.getDefender().getInvolvements();
		} else {
			losers = roll.getAttacker().getInvolvements();
			winners = roll.getDefender().getInvolvements();
		}
		for (Involvement i : winners) {
			this.damageCountry(Rules.COST_OF_WAR_WINNER, i);
		}
	}

	private void damageCountry(Double costOfWar, Involvement i) {
		Country c = i.getCountry();
		// Map the country
		Map<String, Map<String, Map<String, Double>>> attributes = countryServ.mapCountry(c);
		// Apply the damage
		for (String subscenario : attributes.keySet()) {
			Map<String, Map<String, Double>> subscenarioAttributes = attributes.get(subscenario);			
			for (String type : subscenarioAttributes.keySet()) {
				Map<String, Double> typeAttributes = subscenarioAttributes.get(type);
				for (String key : typeAttributes.keySet()) {
					// TODO: ACUMULAR EL COST OF WAR
					Double d = typeAttributes.get(key) * (1 - costOfWar);
					typeAttributes.put(key, d);
				}
			}
		}
		// Write the map back into the file
		// TODO: IMPLEMENT THIS
		countryServ.demapCountry(attributes);
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

	@Override
	public War findById(String warId) {
		Long id;
		try {
			id = Long.parseLong(warId);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		}
		Optional<War> war = warRep.findById(id);
		return (war.isPresent() ? war.get() : null);
	}

	@Override
	public War findOpenWar(Game game) {
		List<War> wars = this.findByGame(game);
		for (War w : wars) {
			if (!Rules.CLOSED_WAR.equals(w.getStatus())) {
				return w;
			}
		}
		return null;
	}

	@Override
	public List<War> findByGame(Game game) {
		List<Turn> turns = turnRep.findByGame(game);
		List<War> wars = new ArrayList<>();
		for (Turn t : turns) {
			List<War> turnWars = warRep.findByTurn(t);
			wars.addAll(turnWars);
		}
		return wars;
	}

	@Override
	public List<War> findByTurn(Turn turn) {
		return warRep.findByTurn(turn);
	}
}
