package es.upo.tfg.rol.controller.controllers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.Rules;
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

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.invalidate();
		return "redirect:/";
	}

	@PostMapping("/loginSubmit")
	public String submitLogin(
			@RequestParam(name = "nickname", required = true, defaultValue = "") String name,
			@RequestParam(name = "pass", required = true, defaultValue = "") String pass,
			Model model, HttpSession session) {
		String hashPassword = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			hashPassword = this.hashPassword(md, pass);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		User user = uServ.findByLogin(name, hashPassword);
		if (user == null) {
			model.addAttribute("loginfail", "Credenciales incorrectas");
			return "index";
		} else {
			session.setAttribute("user", user);
			return "redirect:/landing";
		}
	}

	@PostMapping("/register")
	public String submitRegister(@ModelAttribute @Valid User user,
			BindingResult bindingResult, HttpSession session,
			@RequestParam(name = "avatar_image", required = true) MultipartFile avatar,
			Model model) {
		// Check if the nickname is in use
		if (avatar.getSize() >= Rules.MAX_FILE_SIZE) {
			model.addAttribute("filesize", "El tamaño máximo de la imagen es de 1 MB");
		}
		boolean nicknameExists = (uServ.findByNickname(user.getNickname()) != null);
		if (!user.getNickname().matches("^[\\p{L}0-9]*$")) {
			bindingResult.rejectValue("nickname", "nickname.unavailable",
					"El apodo solo puede contener caracteres alfanuméricos");
		}
		if (nicknameExists) {
			bindingResult.rejectValue("nickname", "nickname.unavailable",
					"El apodo ya existe");
		}
		// Hash password
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String hashPassword = this.hashPassword(md, user.getPassword());
			user.setPassword(hashPassword);
		} catch (NoSuchAlgorithmException e) {
			bindingResult.rejectValue("password", "password.unavailable",
					"Contraseña no válida");
			e.printStackTrace();
		}
		// Reject used nicknames or bad formats
		if (bindingResult.hasErrors() || model.asMap().get("filesize") != null) {
			return "register";
		}
		User registeredUser = uServ.register(user, avatar);
		session.setAttribute("user", registeredUser);
		return "redirect:/landing";
	}

	@PostMapping("/updateUser")
	public String submitUpdate(@ModelAttribute @Valid User user,
			BindingResult bindingResult, HttpSession session,
			@RequestParam(name = "avatar_image", required = true) MultipartFile avatar,
			Model model) {
		User currentUser = (User) session.getAttribute("user");
		if (!Objects.equals(user.getNickname(), currentUser.getNickname())) {
			boolean nicknameExists = (uServ.findByNickname(user.getNickname()) != null);
			if (nicknameExists) {
				bindingResult.rejectValue("nickname", "nickname.unavailable",
						"El apodo ya está en uso");
			}
			if (!user.getNickname().matches("^[\\p{L}0-9]*$")) {
				bindingResult.rejectValue("nickname", "nickname.unavailable",
						"El apodo solo puede contener caracteres alfanuméricos");
			}
		}
		if (avatar.getSize() >= Rules.MAX_FILE_SIZE) {
			model.addAttribute("filesize", "El tamaño máximo de la imagen es de 1 MB");
		}
		// Hash password
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			String hashPassword = this.hashPassword(md, user.getPassword());
			user.setPassword(hashPassword);
		} catch (NoSuchAlgorithmException e) {
			bindingResult.rejectValue("password", "password.unavailable",
					"Contraseña no válida");
			e.printStackTrace();
		}
		if (bindingResult.hasErrors() || model.asMap().get("filesize") != null) {
			return "profile";
		}
		user.setId(currentUser.getId());
		User newUser = uServ.register(user, avatar);
		session.setAttribute("user", newUser);
		return "redirect:/landing";
	}

	private String hashPassword(MessageDigest md, String password) {
		String generatedPassword = null;
		byte[] bytes = md.digest(password.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		generatedPassword = sb.toString();
		return generatedPassword;
	}

}