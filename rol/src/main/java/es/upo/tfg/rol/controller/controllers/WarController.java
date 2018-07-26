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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.War;

@Controller
public class WarController {

	@Autowired
	CountryService cServ;
	@Autowired
	WarService wServ;
	@Autowired
	TurnService tServ;

	@PostMapping("/war")
	public String createWar(HttpSession session, Model model) {
		Game game = (Game) session.getAttribute("game");
		if (game == null) {
			return Access.reject();
		}
		List<Country> countries = cServ.findCountries(game);
		List<Turn> turns = tServ.findTurns(game);
		War war = wServ.createWar(game);
		session.setAttribute("war", war);
		model.addAttribute("war", war);
		model.addAttribute("countries", countries);
		model.addAttribute("turns", turns);
		return "war";
	}

	@PostMapping("/endWar")
	public String endWar(HttpSession session) {
		War war = (War) session.getAttribute("war");
		wServ.endWar(war);
		// TODO: Redirect to game_main
		return "redirect:/landing";
	}

	@PostMapping("/createRoll")
	public String createRoll(HttpSession session, Model model,
			@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "attacker_score", required = true) Double attackerScore,
			@RequestParam(name = "defender_score", required = true) Double defenderScore,
			@RequestParam(name = "attacker_countries", required = true) String attackerCountries,
			@RequestParam(name = "attacker_name", required = true) String attackerName,
			@RequestParam(name = "defender_countries", required = true) String defenderCountries,
			@RequestParam(name = "defender_name", required = true) String defenderName,
			@RequestParam(name = "subscenario", required = true) String subscenario) {
		Game game = (Game)session.getAttribute("game");
		War war = (War) session.getAttribute("war");
		Roll roll = wServ.roll(game, war, name, attackerScore, defenderScore, attackerCountries, defenderCountries, defenderName, attackerName, subscenario);
		if (roll==null) {
			// TODO: Haandle logic error
			return "redirect:/landing";
		} else {
			model.addAttribute("roll", roll);
			return "redirect:/landing";
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
