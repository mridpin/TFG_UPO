package es.upo.tfg.rol.controller.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.controller.controllers.validators.GameValidator;
import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.GameService;
import es.upo.tfg.rol.controller.service.RollService;
import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.exceptions.NotAuthorized;
import es.upo.tfg.rol.model.pojos.Coalition;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
import es.upo.tfg.rol.model.pojos.Scenario;
import es.upo.tfg.rol.model.pojos.Turn;
import es.upo.tfg.rol.model.pojos.User;
import es.upo.tfg.rol.model.pojos.War;

@Controller
public class GameController {

	@Autowired
	CountryService cServ;
	@Autowired
	GameService gServ;
	@Autowired
	UserService uServ;
	@Autowired
	TurnService tServ;
	@Autowired
	WarService wServ;
	@Autowired
	RollService rServ;
	@Autowired
	ScenarioService scServ;

	@GetMapping("/create_game")
	public String createGame(Model model) {
		List<Scenario> scenarios = scServ.findAllScenarios();
		model.addAttribute("turn", new Turn());
		model.addAttribute("scenarios", scenarios);
		return "create_game";
	}

	@GetMapping("/openGame")
	public String openGame(HttpSession session, Model model,
			@RequestParam(name = "game_id", required = true) String gameId,
			@RequestParam(name = "war_id", required = false) Long warId, 
			@RequestParam(name = "is_update", required = false) Boolean update) {
		try {
			// Prevents users from spying or modifying other user's games by guessing
			// values in the url, or F12 and changing the html. If they do, they'll be
			// redirected to an error page
			Long id = Long.parseLong(gameId); // throws NumberFormatException
			Game game = gServ.findById(id);
			if (game == null) {
				throw new NotAuthorized("Recurso no encontrado");
			}
			// Add relevant info to the model:
			// 1. Add the game
			model.addAttribute("game", game);
			session.setAttribute("game", game);
			// 2. Add the countries
			List<Country> countries = cServ.findCountries(game);
			model.addAttribute("countries", countries);
			// 3. Add the turns
			List<Turn> turns = tServ.findTurns(game);
			model.addAttribute("turns", turns);
			int index = turns.indexOf(game.getActiveTurn());
			Turn nextTurn = (index != turns.size() - 1) ? turns.get(index + 1) : null;
			model.addAttribute("nextTurn", nextTurn);
			// 4. Add the wars
			List<List<War>> wars = new ArrayList<>();
			for (Turn t : turns) {
				List<War> turnWars = wServ.findByTurn(t);
				wars.add(turnWars);
			}
			model.addAttribute("wars", wars);
			// TODO: UPDATE DESIGN DIAGRAM
			// If there is no open war, "create war" button will show, else it will be a
			// "open ongoing war" button
			War war = wServ.findOpenWar(game);
			if (war != null) {
				model.addAttribute("war", war);
			}
			// 5. Add the last war fought to the model
			if (warId != null) {
				War lastWar = (War) wServ.findById(warId.toString());
				model.addAttribute("lastWar", lastWar);
				Coalition winner = wServ.findWinner(lastWar);
				model.addAttribute("winner", winner);
			}
			// 6. Add the graph data
			List<List<String>> data = gServ.getChartData(game);
			model.addAttribute("data", data);
			// 7. If its an ajax update call, only draw the war list fragment
			if (update != null && update==true) {
				return "game_main :: war_lists";
			} else {
				return "game_main";
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return Access.reject();
		} catch (NotAuthorized e) {
			e.printStackTrace();
			return Access.reject();
		}
	}

	@GetMapping("/landing")
	public String landing(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		List<Game> playedGames = gServ.findOpenGames(user);
		List<Game> closedGames = gServ.findClosedGames(user);
		model.addAttribute("openGames", playedGames);
		model.addAttribute("closedGames", closedGames);
		session.removeAttribute(Rules.GAME_FAIL);
		session.removeAttribute(Rules.COUNTRY_FAIL);
		session.removeAttribute(Rules.ROLL_FAIL);
		return "landing";
	}

	@PostMapping("/removeScenario")
	public String removeScenario(HttpSession session) {
		session.removeAttribute("scenario");
		session.removeAttribute("turns");
		session.removeAttribute("countries");
		session.removeAttribute("newGameName");
		session.removeAttribute("playerName");
		session.removeAttribute(Rules.GAME_FAIL);
		session.removeAttribute(Rules.COUNTRY_FAIL);
		return "redirect:/create_game";
	}

	@PostMapping("/addScenario")
	public String addScenario(
			@RequestParam(name = "scenario_id", required = true) String idString,
			@RequestParam(name = "name", required = true) String name,
			HttpSession session) {
		// Perform validation
		GameValidator validator = new GameValidator();
		if (name.length() < 2 || name.length() > Rules.MAX_NAME_LENGTH) {
			validator.setGameNameError("El nombre debe tener entre 2 y 255 caracteres");
		}
		if (idString == null || "".equals(idString)) {
			validator.setScenarioError("No se ha seleccionado ningún escenario");
		}
		Long id = -1L;
		try {
			id = Long.parseLong(idString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			validator.setScenarioError("No se ha encontrado el escenario que buscabas");
		}
		if (validator.validate()) {
			Scenario scenario = scServ.findById(id);
			if (scenario == null) {
				validator.setScenarioError(
						"No se ha encontrado el escenario que buscabas");
				return "redirect:/create_game";
			} else {
				// Add the scenario to the session
				session.setAttribute("newGameName", name);
				session.setAttribute("scenario", scenario);
				// Create a list of turns with the scenario and add them to the session
				Game game = (Game) session.getAttribute("game");
				List<Turn> turns = tServ.generateTurns(scenario, game);
				session.setAttribute("turns", turns);
				session.removeAttribute(Rules.GAME_FAIL);
				return "redirect:/create_game";
			}
		} else {
			session.setAttribute(Rules.GAME_FAIL, validator);
			return "redirect:/create_game";
		}
	}

	@PostMapping("/removePlayer")
	public String removePlayer(
			@RequestParam(name = "countryIndex", required = true) String stat,
			HttpSession session) {
		int index = Integer.parseInt(stat);
		List<Country> countries = (List<Country>) session.getAttribute("countries");
		List<MultipartFile> files = (List<MultipartFile>) session.getAttribute("files");
		countries.remove(index);
		files.remove(index);
		session.setAttribute("countries", countries);
		session.setAttribute("files", files);
		// session.removeAttribute(Rules.COUNTRY_FAIL);
		return "redirect:/create_game";
	}

	/**
	 * Stores countries and their files in the session. By storing in the session we
	 * don't need to persist the file in the filesystem or database until the game
	 * is created, therefore avoiding orphan files and database entries in case of
	 * browser closing or some other premature exit. You can keep adding players
	 * until the VM runs out of memory and crashes
	 * 
	 * @param nickname
	 *            player nickname to add to the game
	 * @param data
	 *            file with the country data
	 * @param session
	 *            http session
	 * @return to the same page to keep adding players
	 */
	@PostMapping("/addCountry")
	public String createCountry(
			@RequestParam(name = "player_nickname", required = true) String nickname,
			@RequestParam(name = "country_data", required = true) MultipartFile data,
			HttpSession session) {
		// Assemble country with data from file
		User player = uServ.findByNickname(nickname);
		GameValidator validator = new GameValidator();
		User gm = (User) session.getAttribute("user");
		if (Objects.equals(player, gm)) {
			validator.setAddedGMError("Se ha añadido al Game Master como jugador");
		}
		if (player != null) {
			Scenario scenario = (Scenario) session.getAttribute("scenario");
			List<String> fileErrors = cServ.validateCountryFile(scenario, data);
			if (fileErrors.isEmpty()) {
				Country country = cServ.assembleCountry(player, data);
				List<Country> countries = (List<Country>) session
						.getAttribute("countries");
				List<MultipartFile> files = (List<MultipartFile>) session
						.getAttribute("files");
				// Create lists in session if they don't exist. if they do, store the data
				// there
				if (countries == null) {
					countries = new ArrayList<>();
					countries.add(country);
					files = new ArrayList<>();
					files.add(data);
				} else {
					// Validate so players and countries are unique
					List<User> players = new ArrayList<>();
					for (Country c : countries) {
						players.add(c.getPlayer());
					}
					if (players.contains(country.getPlayer())) {
						validator.setDuplicatePlayerError(
								"Ese jugador ya ha sido añadido a la partida");
					}
					if (countries.contains(country)) {
						validator.setDuplicateCountryError(
								"Ese país ya ha sido añadido a la partida");
					}
					if (validator.validate()) {
						players.add(country.getPlayer());
						countries.add(country);
						files.add(data);
					}
				}
				session.setAttribute("countries", countries);
				session.setAttribute("files", files);
			} else {
				validator.setCountryFileError(fileErrors);
			}
		} else {
			validator.setPlayerDoesntExistError(
					"El apodo introducido no corresponde a ningún jugador");
		}
		if (!validator.validate()) {
			session.setAttribute("playerName", nickname);
			session.setAttribute(Rules.COUNTRY_FAIL, validator);
			return "redirect:/create_game";
		}
		session.removeAttribute("playerName");
		session.removeAttribute(Rules.COUNTRY_FAIL);
		return "redirect:/create_game";
	}

	/**
	 * Persist the game and all it's required information, including: 1. Persist the
	 * countries, 2. Persist the associations between player-country-game
	 * 
	 * @return current game page
	 */
	@PostMapping("/submitGame")
	public String createGameSubmit(HttpSession session, RedirectAttributes redirect) {
		Scenario scenario = (Scenario) session.getAttribute("scenario");
		User user = (User) session.getAttribute("user");
		String name = (String) session.getAttribute("newGameName");
		List<Country> countries = (List<Country>) session.getAttribute("countries");
		List<MultipartFile> files = (List<MultipartFile>) session.getAttribute("files");
		List<Turn> turns = (List<Turn>) session.getAttribute("turns");
		GameValidator validator = new GameValidator();
		// Perform validation
		if (countries == null || countries.size() <= 1) {
			validator.setPlayerCountError("Al menos debes añadir dos jugadores");
		}
		if (validator.validate()) {
			Game game = gServ.createGame(name, user, turns, countries, files, scenario);
			if (game == null) {
				// TODO: HANDLE ERROR
			} else {
				session.removeAttribute(Rules.GAME_FAIL);
				session.removeAttribute(Rules.COUNTRY_FAIL);
				session.setAttribute("game", game);
				redirect.addAttribute("game_id", game.getId());
			}
		} else {
			session.setAttribute(Rules.GAME_FAIL, validator);
			session.setAttribute(Rules.COUNTRY_FAIL, validator);
			return "redirect:/create_game";
		}
		return "redirect:/landing";
	}

	@PostMapping("/closeGame")
	public String closeGame(@RequestParam(name = "pass", required = true) String pass,
			HttpSession session, RedirectAttributes redirectAttributes) {
		// TODO: Show error message if incorrect password
		User user = (User) session.getAttribute("user");
		Game game = (Game) session.getAttribute("game");
		if (Objects.equals(pass, user.getPassword())) {
			gServ.closeGame(game);
			session.removeAttribute("game");
			return "redirect:/landing";
		} else {
			redirectAttributes.addAttribute("game_id", game.getId());
			session.setAttribute("wrongPass", "Contraseña no válida");
			return "redirect:/openGame";
		}
	}

	@PostMapping("/nextTurn")
	public String endTurn(HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		// TODO: POST REDIRECT GET THIS
		Game game = (Game) session.getAttribute("game");
		gServ.nextTurn(game);
		redirectAttributes.addAttribute("game_id", game.getId());
		return "redirect:/openGame";
	}

	@GetMapping(value = "/downloads/country", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public void downloadFile(Model model, @Param(value = "id") Long id,
			HttpServletResponse response) {
		Country country = cServ.findById(id);
		if (country != null) {
			String filename = Rules.COUNTRY_FILE_PATH + File.separator
					+ country.getData();
			FileSystemResource file = new FileSystemResource(filename);
			response.setContentType(MediaType.TEXT_PLAIN_VALUE);
			response.setHeader("Content-disposition",
					"attachment; filename=" + country.getName() + ".csv");
			try {
				StreamUtils.copy(file.getInputStream(), response.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
