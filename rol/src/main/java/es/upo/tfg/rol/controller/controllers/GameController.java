package es.upo.tfg.rol.controller.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

	@PostMapping("/removePlayer")
	public String removePlayer(
			@RequestParam(name = "countryIndex", required = true) String stat,
			HttpSession session) {
		int index = Integer.parseInt(stat);
		List<Country> countries = (List<Country>) session
				.getAttribute("countries");
		List<MultipartFile> files = (List<MultipartFile>) session
				.getAttribute("files");
		countries.remove(index);
		files.remove(index);
		session.setAttribute("countries", countries);
		session.setAttribute("files", files);
		return "create_game";
	}

	@PostMapping("/addCountry")
	public String createCountry(
			@RequestParam(name = "player_nickname", required = true) String nickname,
			@RequestParam(name = "country_data", required = true) MultipartFile data,
			HttpSession session) {
		User player = uServ.findByNickname(nickname);
		// Prepare and store datafile
		String filename = System.currentTimeMillis() + "-"
				+ StringUtils.cleanPath(data.getOriginalFilename());
		Path dataPath = Paths.get("countryData");
		Country country = cServ.assembleCountry(player, data);
		List<Country> countries = (List<Country>) session
				.getAttribute("countries");
		// By storing in the session we don't need to persist the file in the
		// filesystem until the game is created, therefore avoiding orfaned
		// files
		List<MultipartFile> files = (List<MultipartFile>) session
				.getAttribute("files");
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
	 * Persist the game and all it's required information
	 * 
	 * @return current game page
	 */
	@PostMapping("/create_game")
	public String createGameSubmit() {
		// TODO: Reject file if it's not a CSV
		// TODO: reject file if it doesn't comply with format, explaining why if
		// possible
		// try (InputStream inputStream = data.getInputStream()) {
		// Files.copy(inputStream, dataPath.resolve(filename),
		// StandardCopyOption.REPLACE_EXISTING);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		return "game_main";
	}
}
