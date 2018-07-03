package es.upo.tfg.rol.controller.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class UserController {

	@Autowired
	UserService uServ;

	@GetMapping("/")
	public String index() {
		return "index";
	}

	@GetMapping("/register")
	public String register(@ModelAttribute User user) {
		return "register";
	}

	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
		model.addAttribute("user", session.getAttribute("user"));
		return "profile";
	}

	@GetMapping("/landing")
	public String landing() {
		return "landing";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		return "index";
	}

	@PostMapping("/login")
	public String submitLogin(@RequestParam(name = "nickname", required = true, defaultValue = "") String name,
			@RequestParam(name = "pass", required = true, defaultValue = "") String pass, HttpSession session) {
		User user = uServ.findByLogin(name, pass);
		if (user == null) {
			return "index";
		} else {
			session.setAttribute("user", user);
			return "landing";
		}
	}

	@PostMapping("/register")
	public String submitRegister(@ModelAttribute User user, HttpSession session,
			@RequestParam(name = "avatar_image", required = true) MultipartFile avatar) {
		String filename = System.currentTimeMillis() + "-" + StringUtils.cleanPath(avatar.getOriginalFilename());
		Path avatarPath = Paths.get("userImages");
		try (InputStream inputStream = avatar.getInputStream()) {
			Files.copy(inputStream, avatarPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		user.setAvatar(filename);
		uServ.saveUser(user);
		session.setAttribute("user", user);
		return "landing";
	}

	@PostMapping("/updateUser")
	public String submitUpdate(@ModelAttribute User user, HttpSession session,
			@RequestParam(name = "avatar_image", required = true) MultipartFile avatar) {
		user.setId(((User) session.getAttribute("user")).getId());
		if (!"".equals(avatar.getOriginalFilename())) {
			String filename = ((User) session.getAttribute("user")).getAvatar();
			Path avatarPath = Paths.get("userImages");
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, avatarPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
			user.setAvatar(filename);
		} else {
			user.setAvatar(((User) session.getAttribute("user")).getAvatar());
		}
		uServ.saveUser(user);
		session.setAttribute("user", user);
		return "landing";
	}
}