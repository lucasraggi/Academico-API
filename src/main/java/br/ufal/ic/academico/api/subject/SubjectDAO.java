package br.ufal.ic.academico.api.subject;

import br.ufal.ic.academico.api.GenericDAO;
import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.teacher.Teacher;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;

public class SubjectDAO extends GenericDAO<Subject> {

    public SubjectDAO(SessionFactory sessionFactory) { super(sessionFactory); }

    public ArrayList<Subject> getAll() {
        return(ArrayList<Subject>) currentSession().createQuery("from Subject").list();
    }

    public ArrayList<Student> getStudentsInSubject(Subject subject) {
        return((ArrayList<Student>) currentSession()
                .createQuery("select p from Student p where :subjectId in (select s.subject.id from p.subjects s)")
                .setParameter("subjectId", subject.getId())
                .list());
    }

    public Course getCourse(Subject subject) {
        ArrayList<Course> courses = (ArrayList<Course>) currentSession().createQuery("from Course").list();

        for (Course c : courses) {
            assert c.getSubjects() != null;
            for (Subject d : c.getSubjects())
                if (d.getId().equals(subject.getId()))
                    return c;

        }

        return null;
    }

    public List<Subject> getAllByStudent(Student s) {
        List<Subject> subjects = new ArrayList<>();
        List<Subject> allSubjects = this.getAll();

        for (Subject subject : allSubjects)
            if (subject.students.contains(s))
                subjects.add(subject);

        return subjects;
    }

    public Secretary getSecretary(Subject subject) {
        Course course = this.getCourse(subject);

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

    public Department getDepartment(Subject subject) {
        Secretary secretary = this.getSecretary(subject);

        SecretaryDAO secretaryDAO = new SecretaryDAO(currentSession().getSessionFactory());
        return secretaryDAO.getDepartment(secretary);
    }

    public void deallocateTeacherFromAllSubjects(Teacher t) {
        List<Subject> allSubjects = this.getAll();
        for (Subject d : allSubjects) {
            assert d.teacher != null;
            if (d.teacher.getId().equals(t.getId())) {
                d.teacher = null;
                this.persist(d);
            }
        }
    }
}
