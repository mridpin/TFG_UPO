package es.upo.tfg.rol.controller.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
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
		War war = (War)session.getAttribute("war");
		wServ.endWar(war);
		// TODO: Redirect to game_main 
		return "redirect:/landing";		
	}

}
