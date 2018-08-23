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
	@Autowired
	private RollService rollServ;

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
			String defenderName, String attackerName, Turn turn) {
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
		roll.setAttackerRoll(attackerRoll);
		roll.setDefenderRoll(defenderRoll);
		roll.setWar(war);
		rollRep.save(roll);
		// Cost of war penalty
		this.applyCostOfWar(roll);
		// Return the new roll
		return roll;
	}

	/**
	 * Applies damage to the attributes of the attackers and defenders of the roll
	 * 
	 * @param roll
	 */
	private void applyCostOfWar(Roll roll) {
		// Separate countries into winners and losers
		List<Involvement> winners;
		List<Involvement> losers;
		if (roll.getAttackerScore() > roll.getDefenderScore()) {
			Coalition attacker = roll.getAttacker();
			winners = invRep.findByCoalition(attacker);
			Coalition defender = roll.getDefender();
			losers = invRep.findByCoalition(defender);
		} else {
			Coalition attacker = roll.getAttacker();
			losers = invRep.findByCoalition(attacker);
			Coalition defender = roll.getDefender();
			winners = invRep.findByCoalition(defender);
		}
		//
		Map<String, List<Involvement>> involvements = this.arrangeInvolvements(winners,
				losers);
		for (Involvement i : winners) {
			i.setWonTheRoll(new Boolean(true));
			// this.damageCountry(Rules.COST_OF_WAR_WINNER, i);
		}
		for (Involvement i : losers) {
			i.setWonTheRoll(new Boolean(false));
			// this.damageCountry(Rules.COST_OF_WAR_LOSER, i);
		}
		for (String countryName : involvements.keySet()) {
			this.damageCountry(involvements.get(countryName));
		}
	}

	/**
	 * Organizes the winners and losers into a map of lists, where every map entry
	 * is a country of the game that participated in a roll, and every list element
	 * one of its involvements
	 * 
	 * @param winners
	 *            countries in roll
	 * @param losers
	 *            countries in roll
	 * @return map of lists of involvements
	 */
	private Map<String, List<Involvement>> arrangeInvolvements(List<Involvement> winners,
			List<Involvement> losers) {
		Map<String, List<Involvement>> involvements = new HashMap<>();
		for (Involvement i : winners) {
			String name = i.getCountry().getName();
			if (!involvements.containsKey(name)) {
				List<Involvement> involvementsForCountry = new ArrayList<>();
				involvementsForCountry.add(i);
				involvements.put(name, involvementsForCountry);
			} else {
				involvements.get(name).add(i);
			}
		}
		for (Involvement i : losers) {
			String name = i.getCountry().getName();
			if (!involvements.containsKey(name)) {
				List<Involvement> involvementsForCountry = new ArrayList<>();
				involvementsForCountry.add(i);
				involvements.put(name, involvementsForCountry);
			} else {
				involvements.get(name).add(i);
			}
		}
		return involvements;
	}

	/**
	 * Applies damage to a country according to its involvements in a roll
	 *
	 * @param i
	 *            involvements of the country for the roll. contains the country and
	 *            its participation percent
	 */
	private void damageCountry(List<Involvement> involvements) {
		// Map the country
		int n = 0;
		Double leftoverInvolvement = 1.0;
		Country c = involvements.get(0).getCountry();
		Map<String, Map<String, Map<String, Double>>> currentAttributes = countryServ
				.mapCountry(c);
		// Map<String, Map<String, Map<String, Double>>> newAttributes = countryServ
		// .mapCountry(c);
		List<Map<String, Map<String, Map<String, Double>>>> countryAttributes = new ArrayList<>();

		for (Involvement i : involvements) {
			// Copy the starting resources to calculate what each partition consumes
			Map<String, Map<String, Map<String, Double>>> newAttributes = new HashMap<>();
			newAttributes = this.copyMap(currentAttributes);
			Double costOfWar = (i.isWonTheRoll()) ? Rules.COST_OF_WAR_WINNER
					: Rules.COST_OF_WAR_LOSER;
			Double involvement = i.getInvolvementPercent();
			leftoverInvolvement -= involvement;
			// Save the results into the new map
			for (String subscenario : currentAttributes.keySet()) {
				Map<String, Map<String, Double>> subscenarioAttributes = currentAttributes
						.get(subscenario);
				for (String type : subscenarioAttributes.keySet()) {
					Map<String, Double> typeAttributes = subscenarioAttributes.get(type);
					for (String key : typeAttributes.keySet()) {
						if (n == 0) {
							Double originalValue = typeAttributes.get(key);
							// These are the remaining resources for the this involvement
							// partition
							Double d = originalValue * involvement * (1 - costOfWar);
							newAttributes.get(subscenario).get(type).put(key, d);
						}
					}
				}
			}
			countryAttributes.add(newAttributes);
		}
		// Merge all the maps
		Map<String, Map<String, Map<String, Double>>> remainingAttributes = this
				.mergeCountryPartitions(countryAttributes, leftoverInvolvement,
						currentAttributes);
		countryServ.demapCountry(remainingAttributes, c);
	}

	/**
	 * Sums all the partitions of a country that participated in a war, plus all its
	 * unused resources for that war
	 * 
	 * @param countryAttributes
	 *            list of maps that represent country partitions
	 * @param leftoverInvolvement
	 *            percent of unused resources
	 * @param currentAttributes
	 *            original attributes at the start of the roll
	 * @return map which is a sum of the remaining resources from each partition
	 */
	private Map<String, Map<String, Map<String, Double>>> mergeCountryPartitions(
			List<Map<String, Map<String, Map<String, Double>>>> countryAttributes,
			Double leftoverInvolvement,
			Map<String, Map<String, Map<String, Double>>> currentAttributes) {
		Map<String, Map<String, Map<String, Double>>> result = new HashMap<>();
		// Copy the item 0 then start adding from item 1
		result = copyMap(countryAttributes.get(0));
		for (String subscenario : result.keySet()) {
			Map<String, Map<String, Double>> subscenarioAttributes = result
					.get(subscenario);
			for (String type : subscenarioAttributes.keySet()) {
				Map<String, Double> typeAttributes = subscenarioAttributes.get(type);
				for (String key : typeAttributes.keySet()) {
					Double originalValue = typeAttributes.get(key);
					Double currentValue = originalValue;
					for (int i = 1; i < countryAttributes.size(); i++) {
						Double valueToAdd = countryAttributes.get(i).get(subscenario)
								.get(type).get(key);
						currentValue += valueToAdd;
					}
					Double valuesBeforeDamage = currentAttributes.get(subscenario)
							.get(type).get(key);
					Double newValue = currentValue
							+ valuesBeforeDamage * leftoverInvolvement;
					typeAttributes.put(key, newValue);
				}
			}
		}
		return result;
	}

	/**
	 * Finds a country in a list of countries from a game by name.
	 * 
	 * @param name
	 *            of the country
	 * @param countries
	 *            that participated in a game
	 * @return the Country if it exists, null if it doesn't
	 */
	private Country findCountryByName(String name, List<Country> countries) {
		for (Country c : countries) {
			if (c.getName().equals(name)) {
				return c;
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

	@Override
	public Map<String, Map<String, Map<String, Double>>> copyMap(
			Map<String, Map<String, Map<String, Double>>> map) {
		Map<String, Map<String, Map<String, Double>>> copy = new HashMap<>();
		for (String subscenario : map.keySet()) {
			Map<String, Map<String, Double>> subscenarioAttributes = map.get(subscenario);
			Map<String, Map<String, Double>> copySubscenarioAttributes = new HashMap<>();
			for (String type : subscenarioAttributes.keySet()) {
				Map<String, Double> typeAttributes = subscenarioAttributes.get(type);
				Map<String, Double> copyTypeAttributes = new HashMap<>();
				for (String key : typeAttributes.keySet()) {
					Double d = new Double(typeAttributes.get(key));
					copyTypeAttributes.put(key, d);
				}
				copySubscenarioAttributes.put(type, copyTypeAttributes);
			}
			copy.put(subscenario, copySubscenarioAttributes);
		}
		return copy;
	}

	@Override
	public Coalition findWinner(War war) {
		if (war == null) {
			return null;
		}
		Double attackerScore = 0.0;
		Double defenderScore = 0.0;
		Coalition attacker = null;
		Coalition defender = null;
		for (Roll roll : war.getRolls()) {
			attackerScore += roll.getAttackerScore();
			defenderScore += roll.getDefenderScore();
			// Returns the attacker and defender coalitions in their latest state
			attacker = roll.getAttacker();
			defender = roll.getDefender();
		}
		return (attackerScore > defenderScore) ? attacker : defender;
	}
}
