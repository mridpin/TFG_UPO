package es.upo.tfg.rol.controller.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.RollService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;
import es.upo.tfg.rol.model.pojos.War;

@Controller
public class WarController {

	@Autowired
	CountryService cServ;
	@Autowired
	WarService wServ;
	@Autowired
	TurnService tServ;
	@Autowired
	RollService rServ;

	@GetMapping("/war")
	public String createWar(HttpSession session, Model model,
			@RequestParam(name = "war_id") String warId) {
		Game game = (Game) session.getAttribute("game");
		User user = (User) session.getAttribute("user");
		Roll lastRoll = null;
		// Deny access to prevent users from accessing other user's wars by HTML
		// tampering with the warID
		if (game == null || user == null || !user.equals(game.getMaster())) {
			return Access.reject();
		}
		List<Country> countries = cServ.findCountries(game);
		List<Turn> turns = tServ.findTurns(game);
		// If there is no id, it means a new war must be created
		// TODO: UPDATE DESIGN DIAGRAM
		War war;
		List<Roll> rolls = new ArrayList<>();
		if (warId==null || "".equals(warId)) {
			war = wServ.createWar(game);
		} else {
			war = wServ.findById(warId);
			rolls = rServ.findByWar(war);
			lastRoll = rolls.get(rolls.size() - 1);
		}
		if (war==null) {
			return Access.reject();
		}		
		session.setAttribute("war", war);
		model.addAttribute("war", war);
		model.addAttribute("rolls", rolls);
		model.addAttribute("lastRoll", lastRoll);
		model.addAttribute("countries", countries);
		model.addAttribute("turns", turns);
		return "war";
	}

	@GetMapping("/endWar")
	public String endWar(HttpSession session, RedirectAttributes redirectAttributes) {
		Game game = (Game) session.getAttribute("game");
		War war = (War) session.getAttribute("war");
		wServ.endWar(war);
		// TODO: Calculate and show the winning coalition
		redirectAttributes.addAttribute("game_id", game.getId());
		return "redirect:/openGame";
	}

	@PostMapping("/createRoll")
	public String createRoll(HttpSession session, Model model, RedirectAttributes redirectAttributes,
			@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "attacker_score", required = true) Double attackerScore,
			@RequestParam(name = "defender_score", required = true) Double defenderScore,
			@RequestParam(name = "attacker_countries", required = true) String attackerCountries,
			@RequestParam(name = "attacker_name") String attackerName,
			@RequestParam(name = "defender_countries", required = true) String defenderCountries,
			@RequestParam(name = "defender_name") String defenderName,
			@RequestParam(name = "subscenario", required = true) String subscenario) {
		// TODO: PETA CUANDO LA GUERRA YA EXISTE
		Game game = (Game) session.getAttribute("game");
		War war = (War) session.getAttribute("war");
		Roll roll = wServ.roll(game, war, name, attackerScore, defenderScore,
				attackerCountries, defenderCountries, defenderName, attackerName,
				subscenario);
		if (roll == null) {
			// TODO: Handle logic error
			return "redirect:/landing";
		} else {
			int nRolls = rServ.findByWar(war).size();
			redirectAttributes.addAttribute("war_id", war.getId());
			if (nRolls < Rules.MAX_ROLLS_PER_WAR) {
				return "redirect:/war";
			} else {
				return "redirect:/endWar";
			}
		}
	}

	/**
	 * Ajax call that returns a map of maps of all the participating countries to
	 * dynamically display in the browser. There cannot be two countries with the
	 * same name in the same game, so it is safe to use their names as key
	 * 
	 * @return a map attributes where string is the country name and map is its
	 *         attributes
	 */
	@PostMapping(value = "/mapCountries", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Map<String, Map<String, Map<String, Double>>>> mapCountry(
			HttpSession session) {
		Game game = (Game) session.getAttribute("game");
		List<Country> countries = cServ.findCountries(game);
		Map<String, Map<String, Map<String, Map<String, Double>>>> maps = new HashMap<>();
		for (Country c : countries) {
			Map<String, Map<String, Map<String, Double>>> cmap = cServ.mapCountry(c);
			maps.put(c.getName(), cmap);
		}
		return maps;
	}

}
