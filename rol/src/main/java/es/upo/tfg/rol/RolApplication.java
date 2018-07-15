
package es.upo.tfg.rol;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; // TFG MVP
import org.springframework.context.annotation.Bean;

import es.upo.tfg.rol.controller.service.UserServiceImpl;
import es.upo.tfg.rol.model.pojos.User;

@SpringBootApplication // Spring automatically checks application.properties file for configs (@Config
						// is included in this annotation)
public class RolApplication {

	public static void main(String[] args) {
		SpringApplication.run(RolApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner demo(UserServiceImpl userServ) {
//		return (args) -> {
//
//			User u1 = new User();
//			u1.setName("cabesa");
//			u1.setNickname("watup");
//			u1.setAvatar("watup");
//			u1.setPassword("watup");
//
//			userServ.saveUser(u1);
//			for (User u : userServ.findAllUsers()) {
//				System.out.println(u.toString());
//			}
//			u1.setName("wwhat the duck is this");
//			userServ.saveUser(u1);
//			for (User u : userServ.findAllUsers()) {
//				System.out.println(u.toString());
//			}
//			User u3 = userServ.findByLogin("watup", "hunter2");
//			System.out.println(u3.toString());
//			
//			User u4 = userServ.findById(6L);
//			System.out.println(u4.toString());
//		};
//	}
}
