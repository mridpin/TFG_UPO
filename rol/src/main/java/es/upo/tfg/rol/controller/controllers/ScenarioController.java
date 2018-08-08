package es.upo.tfg.rol.controller.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class ScenarioController {

	public ScenarioController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private ScenarioService scServ;

	@PostMapping("/createScenario")
	public String createScenaro(HttpSession session, Model model,
			@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "description", required = true) String description,
			@RequestParam(name = "country_data", required = true) MultipartFile data) {	
		User user = (User)session.getAttribute("user");
		Scenario scenario = scServ.createScenario(name, description, data, user);
		model.addAttribute("scenario", scenario);
		// TODO: POST REDIRECT GET THIS
		return "redirect:/landing";	
	}

}
