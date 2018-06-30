package es.upo.tfg.rol;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import es.upo.tfg.rol.config.AppConfig;
//import es.upo.tfg.rol.controller.service.UserServiceImpl;
import es.upo.tfg.rol.model.dao.UserDAO;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootApplication
public class RolApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserDAO userServ) {
		return (args) -> {
			//AbstractApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

			//UserServiceImpl userServ = (UserServiceImpl) context.getBean("userService");

			User u1 = new User();
			u1.setName("cabesa");
			u1.setNickname("watup");
			u1.setAvatar("watup");
			u1.setPassword("watup");

			userServ.save(u1);
			for (User u : userServ.findAll()) {
				System.out.println(u.toString());
			}
			//userServ.delete(u1);
			u1.setName("wwhat the duck");
			userServ.save(u1);
			for (User u : userServ.findAll()) {
				System.out.println(u.toString());
			}
			userServ.delete(u1);
			//context.close();
		};
	}
}
