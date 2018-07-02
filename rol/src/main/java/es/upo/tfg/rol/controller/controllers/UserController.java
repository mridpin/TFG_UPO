package es.upo.tfg.rol.controller.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String submitRegister(@ModelAttribute User user, HttpSession session) {
		uServ.saveUser(user);
		session.setAttribute("user", user);
		return "landing";
	}

	@PostMapping("/updateUser")
	public String submitUpdate(@ModelAttribute User user, HttpSession session) {
		user.setId(((User) session.getAttribute("user")).getId());
		uServ.saveUser(user);
		session.setAttribute("user", user);
		return "landing";
	}
}