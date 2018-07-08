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

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.controller.service.CountryService;
import es.upo.tfg.rol.controller.service.GameService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.Country;
import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class GameController {

	@Autowired
	CountryService cServ;
	@Autowired
	GameService gServ;
	@Autowired
	UserService uServ;

	@GetMapping("/create_game")
	public String createGame() {
		return "create_game";
	}

	@GetMapping("/openGame")
	public String openGame(HttpSession session, Model model,
			@RequestParam(name = "gameIndex", required = true) String stat) {
		// TODO: RETOMAR AQUÍ: Hay que identificar qué partida se ha abierto para consultar sus paises
		//List<Country> countries = gServ.findCountries();
		return "game_main";
	}

	@GetMapping("/landing")
	public String landing(HttpSession session, Model model) {
		User user = (User) session.getAttribute("user");
		List<Game> playedGames = gServ.findMasteredGames(user);
		model.addAttribute("games", playedGames);
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
		return "create_game";
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
		Country country = cServ.assembleCountry(player, data);
		List<Country> countries = (List<Country>) session.getAttribute("countries");
		List<MultipartFile> files = (List<MultipartFile>) session.getAttribute("files");
		// Create lists in session if they dont exist. if they do, store the data there
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

		return "create_game";
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
		return "game_main";
	}

	@PostMapping("/closeGame")
	public String closeGame() {
		return "create_game";
	}
}