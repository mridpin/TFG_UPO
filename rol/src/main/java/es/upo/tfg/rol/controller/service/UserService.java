package es.upo.tfg.rol.controller.service;

import javax.validation.Valid;

import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.model.pojos.User;

public interface UserService {

	Iterable<User> findAllUsers();

	User findById(Long id);

	User findByLogin(String nickname, String password);
	
	User findByNickname(String nickname);

	void saveUser(User user);

	User register(@Valid User user, MultipartFile avatar);

}