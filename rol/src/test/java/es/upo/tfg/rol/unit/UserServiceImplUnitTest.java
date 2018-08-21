package es.upo.tfg.rol.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import es.upo.tfg.rol.TestUtils;
import es.upo.tfg.rol.controller.service.UserService;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceImplUnitTest {

	@Autowired
	private UserService userService;
	
	public String generatedString;
	
	@Before
	public void createRandomString () {
		this.generatedString = TestUtils.createRandomString();
	}

	@Test
	public void registerTest_ok() {
		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		User newUser = userService.register(user, avatar);
		user.setId(newUser.getId());
		assertEquals(user, newUser);
	}

	@Test(expected = DataIntegrityViolationException.class)
	public void registerTest_nicknameExists() {
		// Random nickname to prevent uniqueness constraint
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));

		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		userService.register(user, avatar);
		User user2 = new User("avatar2", "name2 familyname2", generatedString, "password2");
		userService.register(user2, avatar); // Throws exception
	}

	@Test
	public void findByLoginTest_ok() {
		// Random nickname to prevent uniqueness constraint
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));

		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		userService.register(user, avatar);

		User newUser = userService.findByLogin(generatedString, "password");
		assertTrue(newUser != null);
	}

	@Test
	public void findByLogin_null() {
		// Random nickname to prevent uniqueness constraint
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));

		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		userService.register(user, avatar);

		User newUser = userService.findByLogin(generatedString, "wrongpassword");
		assertTrue(newUser == null);
	}
	
	@Test
	public void findByNickname_ok() {
		// Random nickname to prevent uniqueness constraint
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));

		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		userService.register(user, avatar);

		User newUser = userService.findByNickname(generatedString);
		assertTrue(newUser != null);
	}
	
	@Test
	public void findByNickname_null() {
		// Random nickname to prevent uniqueness constraint
		byte[] array = new byte[10];
		new Random().nextBytes(array);
		String generatedString = new String(array, Charset.forName("UTF-8"));

		MockMultipartFile avatar = new MockMultipartFile("data", "filename.jpg",
				"image/jpeg", "some csv".getBytes());
		User user = new User("avatar", "name familyname", generatedString, "password");
		userService.register(user, avatar);

		User newUser = userService.findByNickname(generatedString + "wrongnickname");
		assertTrue(newUser == null);
	}
	
	

}
