package es.upo.tfg.rol.controller.service;

import java.util.List;

import es.upo.tfg.rol.model.pojos.Game;
import es.upo.tfg.rol.model.pojos.User;

public interface UserService {

	Iterable<User> findAllUsers();

	User findById(Long id);

	User findByLogin(String nickname, String password);
	
	User findByNickname(String nickname);

	void saveUser(User user);

}