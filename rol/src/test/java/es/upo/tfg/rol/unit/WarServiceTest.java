package es.upo.tfg.rol.unit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import es.upo.tfg.rol.TestUtils;
import es.upo.tfg.rol.controller.service.ScenarioService;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WarServiceTest {

	@Autowired
	UserService us;
	@Autowired
	ScenarioService ss;

	public User user;

	@Before
	public void createUser() {
		String nickname = TestUtils.createRandomString();
		User user = new User("avatar", "name", nickname, "password");
		us.saveUser(user);
		this.user = user;
	}

}
