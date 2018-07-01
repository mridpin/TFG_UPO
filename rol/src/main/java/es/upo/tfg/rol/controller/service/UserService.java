package es.upo.tfg.rol.controller.service;

import es.upo.tfg.rol.model.pojos.User;

public interface UserService {

	void saveUser(User user);

	Iterable<User> findAllUsers();

	void delete(User user);

	User findById(Long id);

	User findByLogin(String nickname, String password);

	void updateUser(User user);

}