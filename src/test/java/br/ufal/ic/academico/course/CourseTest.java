package br.ufal.ic.academico.course;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.secretary.*;
import br.ufal.ic.academico.api.subject.*;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class CourseTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    private CourseDAO dao = new CourseDAO(dbTesting.getSessionFactory());

    @Test
    void courseCRUD() {
        final Course c1 = create("test name 1");
        final Course c3 = create("test name 2");
        update(c1);
        delete(c1);

        c1.addSubject(new Subject());

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Course wasnt removed");

        assertEquals(c3.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "Get couldn't reach Course 3");
    }


    private Course create(String name) {
        final Course course = new Course(name);
        final Course saved = dbTesting.inTransaction(() -> dao.persist(course));

        assertAll(
                () -> assertNotNull(saved, "failed to save course"),
                () -> assertNotNull(saved.getId(), "Course did not receive an id"),
                () -> assertEquals(name, saved.getName(), "course name could not be attributed"),
                () -> assertNotNull(saved.getSubjects(), "course name could not be attributed")
        );


        return course;
    }


    private void update(Course course) {
        final Course c = dbTesting.inTransaction(() -> dao.persist(course));

        assertAll(
                () -> assertEquals(course.getId(), c.getId(), "updated course id is different"),
                () -> assertEquals(course.getName(), c.getName(), "updated course name is different"),
                () -> assertNotNull(c.getSubjects(), "updated course subjects is different")
        );
    }

    private void delete(Course course) {
        dbTesting.inTransaction(() -> dao.delete(course));
        assertNull(dbTesting.inTransaction(() -> dao.get(course.getId())), "course was not removed");
    }
}