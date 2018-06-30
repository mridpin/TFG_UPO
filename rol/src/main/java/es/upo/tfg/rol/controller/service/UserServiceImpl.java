package es.upo.tfg.rol.controller.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.UserDAO;
import es.upo.tfg.rol.model.pojos.User;
 

 
@Service("userService")
@Transactional
public class UserServiceImpl {
 
    @Autowired
    private UserDAO dao;
     
    public void saveUser(User user) {
        dao.save(user);
    }
 
    public Iterable<User> findAllUsers() {
    	return dao.findAll();
    	//return StreamSupport.stream(dao.findAll().spliterator(), false).collect(Collectors.toList());
    }
 
    public void delete(User user) {
        dao.delete(user);
    }
 
    public User findById(Long id) {
        try {
			return dao.findById(id).get();
		} catch (Exception e) {
			return null;
		}
    }
 
    public void updateUser(User user){
        dao.save(user);
    }
}
