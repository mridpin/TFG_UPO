package es.upo.tfg.rol.controller.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class GameController {
	@GetMapping("/create_game")
	public String createGame(@ModelAttribute Game game) {
		return "create_game";
	}
}
