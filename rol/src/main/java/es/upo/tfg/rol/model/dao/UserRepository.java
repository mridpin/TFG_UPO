package es.upo.tfg.rol.model.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import es.upo.tfg.rol.model.pojos.User;

public interface UserRepository extends CrudRepository<User, Long> {
	// Return Optional<T> to comply with CrudRepository standards
	public Optional<User> findByNickname(final String nickname);

	public Optional<User> findByNicknameAndPassword(final String nickname,
			final String password);
}
