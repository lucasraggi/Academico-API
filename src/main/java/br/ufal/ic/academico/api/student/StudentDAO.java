package br.ufal.ic.academico.api.student;

import br.ufal.ic.academico.api.GenericDAO;
import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StudentDAO extends GenericDAO<Student> {
    public StudentDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public ArrayList<Student> getAll() {
        return (ArrayList<Student>) currentSession().createQuery("from Student").list();
    }

    public Secretary getSecretary(Student student) {
        Course course = student.getCourse();

        SecretaryDAO secretaryDAO = new SecretaryDAO(currentSession().getSessionFactory());

        Secretary secretary = null;
        List<Secretary> secretaries = secretaryDAO.getAll();
        for (Secretary s : secretaries) {
            if (s.getCourses().contains(course)) {
                secretary = s;
                break;
            }
        }
        return secretary;
    }

    public Department getDepartment(Student student) {
        Secretary secretary = this.getSecretary(student);

        SecretaryDAO secretaryDAO = new SecretaryDAO(currentSession().getSessionFactory());
        return secretaryDAO.getDepartment(secretary);
    }
}
