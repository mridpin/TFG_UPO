package es.upo.tfg.rol.controller.controllers;

import java.awt.Image;
import java.io.IOException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.User;

@Controller
public class UserController {

	@Autowired
	UserService uServ;

	String encodedImage;
	
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
	public String submitRegister(@ModelAttribute User user, HttpSession session, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		if (user.getAvatar().length > 1024) {
			uServ.saveUser(user);
			encodedImage = Base64.getEncoder().encodeToString(user.getAvatar());
		}
		session.setAttribute("user", user);
		return "landing";
	}

	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws ServletException {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	@PostMapping("/updateUser")
	public String submitUpdate(@ModelAttribute User user, HttpSession session) {
		user.setId(((User) session.getAttribute("user")).getId());
		uServ.saveUser(user);
		session.setAttribute("user", user);
		return "landing";
	}

	public String getEncodedImage() {
		return encodedImage;
	}

	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}
	
	
}