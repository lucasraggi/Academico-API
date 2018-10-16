package br.ufal.ic.academico;


import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.course.CourseResource;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import br.ufal.ic.academico.api.department.DepartmentResource;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import br.ufal.ic.academico.api.secretary.SecretaryResource;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDAO;
import br.ufal.ic.academico.api.student.StudentResource;
import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDAO;
import br.ufal.ic.academico.api.subject.SubjectResource;
import br.ufal.ic.academico.api.teacher.Teacher;
import br.ufal.ic.academico.api.teacher.TeacherDAO;
import br.ufal.ic.academico.api.teacher.TeacherResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Willy
 */
@Slf4j
public class AcademicoApp extends Application<ConfigApp> {

    public static void main(String[] args) throws Exception {
        new AcademicoApp().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<ConfigApp> bootstrap) {
        log.info("initialize");
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(ConfigApp config, Environment environment) {
        
        final StudentDAO studentDAO = new StudentDAO(hibernate.getSessionFactory());
        final TeacherDAO teacherDAO = new TeacherDAO(hibernate.getSessionFactory());
        final DepartmentDAO departmentDAO = new DepartmentDAO(hibernate.getSessionFactory());
        final SecretaryDAO secretaryDAO = new SecretaryDAO(hibernate.getSessionFactory());
        final CourseDAO courseDAO = new CourseDAO(hibernate.getSessionFactory());
        final SubjectDAO subjectDAO = new SubjectDAO(hibernate.getSessionFactory());

        final StudentResource studentResource = new StudentResource(courseDAO, studentDAO,subjectDAO);
        final DepartmentResource departmentResource = new DepartmentResource(departmentDAO, secretaryDAO);
        final SecretaryResource secretaryResource = new SecretaryResource(departmentDAO, secretaryDAO, courseDAO);
        final CourseResource courseResource = new CourseResource(secretaryDAO, courseDAO, subjectDAO);
        final SubjectResource subjectResource = new SubjectResource(courseDAO, subjectDAO);
        final TeacherResource teacherResource = new TeacherResource(teacherDAO,subjectDAO);

        environment.jersey().register(studentResource);
        environment.jersey().register(departmentResource);
        environment.jersey().register(secretaryResource);
        environment.jersey().register(courseResource);
        environment.jersey().register(subjectResource);
        environment.jersey().register(teacherResource);

    }

    private final HibernateBundle<ConfigApp> hibernate
            = new HibernateBundle<ConfigApp>(Student.class, Teacher.class, Department.class, Secretary.class, Course.class, Subject.class) {
        
        @Override
        public DataSourceFactory getDataSourceFactory(ConfigApp configuration) {
            return configuration.getDatabase();
        }
    };
}
