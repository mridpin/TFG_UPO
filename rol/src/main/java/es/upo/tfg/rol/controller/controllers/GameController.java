package es.upo.tfg.rol.controller.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.GameService;
import es.upo.tfg.rol.controller.service.RollService;
import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.exceptions.NotAuthorized;
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
			@RequestParam(name = "game_id", required = true) String gameId) {
		User user = (User) session.getAttribute("user");
		try {
			// Prevents users from spying or modifying other user's games by guessing
			// values in the url, or F12 and changing the html. If they do, they'll be
			// redirected to an error page
			Long id = Long.parseLong(gameId); // throws NumberFormatException
			Game game = gServ.findById(id);
			if (game == null) {
				throw new NotAuthorized("Recurso no encontrado");
			}
			User master = game.getMaster();
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
			boolean access = Objects.equals(user, master);
			if (!access) {
				return "game_player";
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
		return "landing";
	}

	@PostMapping("/removeScenario")
	public String removeScenario(HttpSession session) {
		session.removeAttribute("scenario");
		session.removeAttribute("turns");
		session.removeAttribute("countries");
		session.removeAttribute("newGameName");
		return "redirect:/create_game";
	}

	@PostMapping("/addScenario")
	public String addScenario(
			@RequestParam(name = "scenario_id", required = true) Long id,
			@RequestParam(name = "name", required = true) String name,
			HttpSession session) {
		// Add the scenario to the session
		session.setAttribute("newGameName", name);
		Scenario scenario = scServ.findById(id);
		session.setAttribute("scenario", scenario);
		// Create a list of turns with the scenario and add them to the session
		Game game = (Game) session.getAttribute("game");
		List<Turn> turns = tServ.generateTurns(scenario, game);
		session.setAttribute("turns", turns);
		return "redirect:/create_game";
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
		// TODO: Validate so player are unique
		// TODO: Validate so countries are unique
		// TODO: Reject file if it's not a CSV
		// TODO: reject file if it doesn't comply with format, explaining why if
		// possible
		// Assemble country with data from file
		User player = uServ.findByNickname(nickname);
		if (player != null) { // TODO: Mostrar mensaje de error aqui si no existe
			Country country = cServ.assembleCountry(player, data);
			List<Country> countries = (List<Country>) session.getAttribute("countries");
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
				countries.add(country);
				files.add(data);
			}
			session.setAttribute("countries", countries);
			session.setAttribute("files", files);
		}
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
		Game game = gServ.createGame(name, user, turns, countries, files, scenario);
		if (game == null) {
			// TODO: HANDLE ERROR
		} else {
			// TODO: post-redirect-get
			session.setAttribute("game", game);
			redirect.addAttribute("game_id", game.getId());
		}
		return "redirect:/landing";
	}

	@PostMapping("/closeGame")
	public String closeGame(@RequestParam(name = "pass", required = true) String pass,
			HttpSession session) {
		// TODO: Show error message if incorrect password
		User user = (User) session.getAttribute("user");
		if (Objects.equals(pass, user.getPassword())) {
			Game game = (Game) session.getAttribute("game");
			gServ.closeGame(game);
			session.removeAttribute("game");
			return "redirect:/landing";
		}
		return "game_main";
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
}
