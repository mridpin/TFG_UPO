package es.upo.tfg.rol.model.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractHibernateDAO<T extends Serializable> {

	protected Class<T> clazz;

	@Autowired
	SessionFactory sessionFactory;

	public final void setClazz(Class<T> clazzToSet) {
		this.clazz = clazzToSet;
	}

	public T find(long id) {
		return getCurrentSession().get(clazz, id);
	}

	public List<T> findAll() {
		return getCurrentSession().createQuery("from " + clazz.getName()).list();
	}

	public void create(T entity) {
		getCurrentSession().persist(entity);
	}

	public void update(T entity) {
		getCurrentSession().merge(entity);
	}

	public void delete(T entity) {
		getCurrentSession().delete(entity);
	}

	protected final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}
}
