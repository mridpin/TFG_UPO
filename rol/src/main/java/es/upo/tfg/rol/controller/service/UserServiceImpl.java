package es.upo.tfg.rol.controller.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.UserDAO;
import es.upo.tfg.rol.model.pojos.User;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDAO dao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upo.tfg.rol.controller.service.UserService#saveUser(es.upo.tfg.rol.model.
	 * pojos.User)
	 */
	@Override
	public void saveUser(User user) {
		dao.save(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upo.tfg.rol.controller.service.UserService#findAllUsers()
	 */
	@Override
	public Iterable<User> findAllUsers() {
		return dao.findAll();
		// return StreamSupport.stream(dao.findAll().spliterator(),
		// false).collect(Collectors.toList());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see es.upo.tfg.rol.controller.service.UserService#findById(java.lang.Long)
	 */
	@Override
	public User findById(Long id) {
		Optional<User> user = dao.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * es.upo.tfg.rol.controller.service.UserService#findByLogin(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public User findByLogin(String nickname, String password) {
		Optional<User> user = dao.findByNicknameAndPassword(nickname, password);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	@Override
	public User findByNickname(String nickname) {
		Optional<User> user = dao.findByNickname(nickname);
		if (user.isPresent()) {
			return user.get();
		}
		return null;		
	}
}
