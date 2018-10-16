package br.ufal.ic.academico.api;

import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import java.util.ArrayList;
import java.io.Serializable;


public abstract class GenericDAO<T> extends AbstractDAO<T> {

    public GenericDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public T get(Serializable id) {
        return super.get(id);
    }

    @Override
    public T persist(T entity) {
        return super.persist(entity);
    }

    public void delete(T entity) {
        super.currentSession().delete(entity);
    }

    abstract public ArrayList<T> getAll();
}