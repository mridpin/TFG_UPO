package es.upo.tfg.rol.controller.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class LoginController {

	@Autowired
	UserService uServ;

	@GetMapping("/")
	public String index() {
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
			return "profile";
		}
	}

	@GetMapping("/register")
	public String submitRegister() {
		return "register";
	}
}