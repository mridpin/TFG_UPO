package es.upo.tfg.rol.controller.controllers;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class ScenarioController {

	public ScenarioController() {
		// TODO Auto-generated constructor stub
	}

	@Autowired
	private ScenarioService scServ;

	@GetMapping("/scenario-resumen")
	public String scenarioPRG(Model model, HttpSession session) {
		Map<String, Map<String, Map<String, Double>>> map = (Map<String, Map<String, Map<String, Double>>>) session
				.getAttribute("map");
		Scenario scenario = (Scenario)session.getAttribute("scenario");
		model.addAttribute("scenario", scenario);
		model.addAttribute("map", map);
		return "scenario-resumen";
	}

	@GetMapping("/createScenario")
	public String createScenario() {
		return "scenario";
	}

	@PostMapping("/createScenarioSubmit")
	public String createScenarioSubmit(HttpSession session, Model model,
			RedirectAttributes redirectAttributes,
			@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "description") String description,
			@RequestParam(name = "data", required = true) MultipartFile data) {
		User user = (User) session.getAttribute("user");
		Scenario scenario = scServ.createScenario(name, description, data, user);
		session.setAttribute("scenario", scenario);
		Map<String, Map<String, Map<String, Double>>> scenarioMap = scServ
				.mapScenario(scenario);
		session.setAttribute("map", scenarioMap);
		return "redirect:/scenario-resumen";
	}

}
