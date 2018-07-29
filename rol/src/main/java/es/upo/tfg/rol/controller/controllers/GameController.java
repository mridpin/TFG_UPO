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

import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.GameService;
import es.upo.tfg.rol.controller.service.RollService;
import es.upo.tfg.rol.controller.service.TurnService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.controller.service.WarService;
import es.upo.tfg.rol.exceptions.NotAuthorized;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.Roll;
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

	@GetMapping("/create_game")
	public String createGame(Model model) {
		model.addAttribute("turn", new Turn());
		return "create_game";
	}

	@GetMapping("/openGame")
	public String openGame(HttpSession session, Model model,
			@RequestParam(name = "game_id", required = true) String gameId)
			throws InterruptedException {
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
			// 4. Add the wars
			List<List<War>> wars = new ArrayList<>();
			for (Turn t : turns) {
				List<War> turnWars = wServ.findByTurn(t);
				wars.add(turnWars);
			}
			model.addAttribute("wars", wars);
			// 5. Add the rolls
			// TODO: RETOMAR AQUI PORQUE LAS DOS ULTIMAS GUERRAS SON IGUALES. APROVECHAR
			// LA NUEVA RELACION COALITION-INVOLVEMENT PARA MOSTRAR TODOS LOS PAISES
			List<List<List<Roll>>> rollsPerGame = new ArrayList<>();
			for (List<War> turnWars : wars) {
				List<List<Roll>> rollsPerTurn = new ArrayList<>();
				for (War w : turnWars) {
					List<Roll> rollsPerWar = rServ.findByWar(w);
					rollsPerTurn.add(rollsPerWar);
				}
				rollsPerGame.add(rollsPerTurn);
			}
			model.addAttribute("rolls", rollsPerGame);
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

	@PostMapping("/removeTurn")
	public String removeTurn(
			@RequestParam(name = "turnIndex", required = true) String stat,
			HttpSession session) {
		// TODO: Throw error if index is tampered with
		int index = Integer.parseInt(stat);
		List<Turn> turns = (List<Turn>) session.getAttribute("turns");
		turns.remove(index);
		session.setAttribute("turns", turns);
		return "redirect:/create_game";
	}

	/**
	 * Stores turns in the session. By storing in the session we don't need to
	 * persist until the game is created, therefore avoiding orphan database entries
	 * in case of browser closing or some other premature exit. You can keep adding
	 * turns until the VM runs out of memory and crashes
	 * 
	 * @param turn
	 *            data of the turn we are creating
	 * @param session
	 *            http session
	 * @return to the same page to keep adding turns or players
	 */
	@PostMapping("/addTurn")
	public String createTurn(HttpSession session, @ModelAttribute Turn turn) {
		List<Turn> turns = (List<Turn>) session.getAttribute("turns");
		if (turns == null) {
			turns = new ArrayList<>();
		}
		turns.add(turn);
		session.setAttribute("turns", turns);
		return "redirect:/create_game";
	}

	/**
	 * Persist the game and all it's required information, including: 1. Persist the
	 * countries, 2. Persist the associations between player-country-game
	 * 
	 * @return current game page
	 */
	@PostMapping("/submitGame")
	public String createGameSubmit(
			@RequestParam(name = "name", required = true) String name,
			@RequestParam(name = "scenario", required = true) String scenario,
			HttpSession session) {
		// TODO: post-redirect-get
		// Create and persist the game
		// TODO: Create game in gameservice
		Game game = new Game();
		game.setMaster((User) session.getAttribute("user"));
		game.setName(name);
		game.setScenario(scenario);
		game.setStartDate(new Date());
		gServ.saveGame(game);
		// Persist the countries
		List<Country> countries = (List<Country>) session.getAttribute("countries");
		List<MultipartFile> files = (List<MultipartFile>) session.getAttribute("files");
		int i = 0;
		for (Country c : countries) {
			// Prepare and store datafile
			MultipartFile f = files.get(i);
			String filename = System.currentTimeMillis() + "-"
					+ StringUtils.cleanPath(f.getOriginalFilename());
			Path dataPath = Paths.get("countryData");
			try (InputStream inputStream = f.getInputStream()) {
				Files.copy(inputStream, dataPath.resolve(filename),
						StandardCopyOption.REPLACE_EXISTING);
				c.setData(filename);
				c.setGame(game);
				cServ.saveCountry(c);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Just in case two iterations occur in the same millisecond
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		// Persist the turns
		List<Turn> turns = (List<Turn>) session.getAttribute("turns");
		for (Turn t : turns) {
			t.setGame(game);
			gServ.saveTurn(t); // TODO: this is provisional, should be on gServ createGame
								// method
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
}
