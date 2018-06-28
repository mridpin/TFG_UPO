package es.upo.tfg.rol.model.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import es.upo.tfg.rol.model.pojos.User;
 
@Repository("userDAO")
public class UserDAOImpl extends AbstractDAO { // Implements userDAO
 
    public void save(User User) {
        persist(User);
    }
 
    @SuppressWarnings("unchecked")
    public List<User> findAll() {
        Criteria criteria = getSession().createCriteria(User.class);
        return (List<User>) criteria.list();
    } 
     
    public User findById(String ssn){
        Criteria criteria = getSession().createCriteria(User.class);
        criteria.add(Restrictions.eq("ssn",ssn));
        return (User) criteria.uniqueResult();
    }
     
    public void update(User User){
        getSession().update(User);
    }
    
    public void delete(User user) {
    	getSession().delete(user);
    }
     
}
