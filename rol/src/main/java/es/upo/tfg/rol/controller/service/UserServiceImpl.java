package es.upo.tfg.rol.controller.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import es.upo.tfg.rol.Rules;
import es.upo.tfg.rol.model.dao.UserRepository;
import es.upo.tfg.rol.model.pojos.User;

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRep;

	@Override
	public User register(@Valid User user, MultipartFile avatar) {
		if ("".equals(avatar.getOriginalFilename())) {
			user.setAvatar(Rules.DEFAULT_USER_IMAGE);
		} else {
			// Store user image in file system and save its path and name
			String filename = System.currentTimeMillis() + "-"
					+ StringUtils.cleanPath(avatar.getOriginalFilename());
			Path avatarPath = Paths.get("userImages");
			try (InputStream inputStream = avatar.getInputStream()) {
				Files.copy(inputStream, avatarPath.resolve(filename),
						StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			user.setAvatar(filename);
		}
		userRep.save(user);
		return user;
	}

	@Override
	public void saveUser(User user) {
		userRep.save(user);
	}

	@Override
	public Iterable<User> findAllUsers() {
		return userRep.findAll();
	}
	
	@Override
	public User findById(Long id) {
		Optional<User> user = userRep.findById(id);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	@Override
	public User findByLogin(String nickname, String password) {
		Optional<User> user = userRep.findByNicknameAndPassword(nickname, password);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

	@Override
	public User findByNickname(String nickname) {
		Optional<User> user = userRep.findByNickname(nickname);
		if (user.isPresent()) {
			return user.get();
		}
		return null;
	}

}
