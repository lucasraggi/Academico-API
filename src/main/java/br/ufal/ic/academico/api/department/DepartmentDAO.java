package br.ufal.ic.academico.api.department;

import br.ufal.ic.academico.api.GenericDAO;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class DepartmentDAO extends GenericDAO<Department> {
    public DepartmentDAO(SessionFactory sessionFactory) { super(sessionFactory); }

    public ArrayList<Department> getAll() {
        return ((ArrayList<Department>) currentSession().createQuery("from Department").list());
    }
}
