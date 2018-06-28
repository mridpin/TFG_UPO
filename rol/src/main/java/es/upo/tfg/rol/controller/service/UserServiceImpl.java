package es.upo.tfg.rol.controller.service;

import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.upo.tfg.rol.model.dao.UserDAOImpl;
import es.upo.tfg.rol.model.pojos.User;
 

 
@Service("userService")
@Transactional
public class UserServiceImpl {
 
    @Autowired
    private UserDAOImpl dao;
     
    public void saveUser(User user) {
        dao.save(user);
    }
 
    public List<User> findAllUsers() {
        return dao.findAll();
    }
 
    public void delete(User user) {
        dao.delete(user);
    }
 
    public User findById(String id) {
        return dao.findById(id);
    }
 
    public void updateUser(User user){
        dao.update(user);
    }
}
