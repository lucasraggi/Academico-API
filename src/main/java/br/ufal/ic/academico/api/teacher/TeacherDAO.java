package br.ufal.ic.academico.api.teacher;

import br.ufal.ic.academico.api.GenericDAO;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class TeacherDAO extends GenericDAO<Teacher> {
    public TeacherDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public ArrayList<Teacher> getAll() {
        return (ArrayList<Teacher>) currentSession().createQuery("from Teacher").list();
    }
}
