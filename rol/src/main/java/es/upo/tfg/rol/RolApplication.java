package es.upo.tfg.rol;

import java.util.List;

//import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import es.upo.tfg.rol.config.AppConfig;
import es.upo.tfg.rol.controller.service.UserServiceImpl;
import es.upo.tfg.rol.model.pojos.User;

//@SpringBootApplication
public class RolApplication {

	public static void main(String[] args) {
		//SpringApplication.run(RolApplication.class, args);
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
		UserServiceImpl userServ = (UserServiceImpl) context.getBean("userService");
		
		User u1 = new User();
		u1.setName("cabesa");
		u1.setNickname("watup");
		u1.setAvatar("watup");
		u1.setPassword("watup");	
	
		
		userServ.saveUser(u1);		
		List<User> users = userServ.findAllUsers();
		for (User u : users) {
			System.out.println(u.toString());
		}
		userServ.delete(u1);
		u1.setName("wwhat the duck");		
		users = userServ.findAllUsers();
		for (User u : users) {
			System.out.println(u.toString());
		}
		context.close();
	}
}
