package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.GenericDAO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.secretary.Secretary;
import org.hibernate.SessionFactory;

import java.util.ArrayList;

public class CourseDAO extends GenericDAO<Course> {

    public CourseDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ArrayList<Course> getAll() {
        return(ArrayList<Course>) currentSession().createQuery("from Course").list();
    }

    public Department getDepartment(Course course) {
        Department department = (Department) currentSession().createQuery(
                "select d from Department d where " +
                        "(:courseid in (select c.id from d.graduate.courses c)) or " +
                        "(:courseid in (select c.id from d.postgraduate.courses c))"
        )
                .setParameter("courseid", course.getId())
                .uniqueResult();
        return(department);
    }

    public Secretary getSecretary(Course course) {
        ArrayList<Secretary> secretaries = (ArrayList<Secretary>) currentSession().createQuery("from Secretary").list();
        for (Secretary s : secretaries) {
            for (Course c : s.getCourses()) {
                if (c.getId().equals(course.getId())) {
                    return s;
                }
            }
        }
        return null;
    }


}
