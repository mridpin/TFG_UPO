package es.upo.tfg.rol.controller.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.PathResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.upo.tfg.rol.Rules;
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

	@GetMapping("/scenario-resumen")
	public String scenarioPRG(Model model, HttpSession session) {
		Map<String, Map<String, Map<String, Double>>> map = (Map<String, Map<String, Map<String, Double>>>) session
				.getAttribute("map");
		Scenario scenario = (Scenario) session.getAttribute("scenario");
		model.addAttribute("scenario", scenario);
		model.addAttribute("map", map);
		return "scenario-resumen";
	}

	@GetMapping(value = "/downloads/scenarios", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public void downloadFile(Model model, @Param(value = "id") Long id,
			HttpServletResponse response) {
		Scenario scenario = scServ.findById(id);
		if (scenario != null) {
			String filename = Rules.SCENARIO_FILE_PATH + File.separator
					+ scenario.getData();
			FileSystemResource file = new FileSystemResource(filename);
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			response.setHeader("Content-disposition",
					"attachment; filename=" + scenario.getName() + ".csv");
			try {
				StreamUtils.copy(file.getInputStream(), response.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
	
	/**
	 * Ajax call that returns a map of the attributes of the game
	 * 
	 * @return a map all the attributes in the scenario file
	 */
	@PostMapping(value = "/mapAttributes", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Map<String, Map<String, Map<String, Double>>> mapScenario(@RequestParam(name = "scenario", required = true) Long id) {
		Scenario scenario = scServ.findById(id);
		Map<String, Map<String, Map<String, Double>>> rules = scServ.mapScenario(scenario);
		return rules;
	}

}
