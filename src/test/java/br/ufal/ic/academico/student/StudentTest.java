package br.ufal.ic.academico.student;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.secretary.*;
import br.ufal.ic.academico.api.subject.*;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(DropwizardExtensionsSupport.class)
class StudentTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    private StudentDAO dao = new StudentDAO(dbTesting.getSessionFactory());

    @Test
    void studentCRUD() {
        Student student1 = create("first name", "last name");
        get(student1);
        update(student1);
        delete(student1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "student was not removed from the database");

        Student student2 = create("first name 1", "last name 1");
        get(student2);
        Student student3 = create("first name 2", "last name 2");
        get(student3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "the students was not created successfully");
    }

    private Student create(String firstName, String lastName) {
        Student student = new Student(firstName, lastName);
        Student studentDB = dbTesting.inTransaction(() -> dao.persist(student));

        assertAll(
                () -> assertNotNull(studentDB, "student created is null"),
                () -> assertNotNull(studentDB.getId(), "student id is null"),
                () -> assertEquals(student.getFirstName(), studentDB.getFirstName(), "student first name is incorrect"),
                () -> assertEquals(student.getLastName(), studentDB.getLastName(), "student last name is incorrect"),
                () -> assertEquals(new ArrayList<>(), studentDB.getCompletedSubjects(), "student completed subjects is incorrect")
        );

        return student;
    }

    private void get(Student student) {
        Student retrieved = dbTesting.inTransaction(() -> dao.get(student.getId()));


        assertAll(
                () -> assertEquals(student.getId(), retrieved.getId(), "student retrieved id is incorrect"),
                () -> assertEquals(student.getFirstName(), retrieved.getFirstName(), "student retrieved first name is incorrect"),
                () -> assertEquals(student.getLastName(), retrieved.getLastName(), "student retrieved last name is incorrect"),
                () -> assertEquals(student.getCredits(), retrieved.getCredits(), "student retrieved credits is incorrect"),
                () -> assertEquals(student.getCompletedSubjects(), retrieved.getCompletedSubjects(), "student retrieved completed subjects is incorrect")
        );
    }

    private void update(Student student) {
        Student updated = dbTesting.inTransaction(() -> dao.persist(student));


        assertAll(
                () -> assertEquals(student.getId(), updated.getId(), "student updated id is incorrect"),
                () -> assertEquals(student.getFirstName(), updated.getFirstName(), "student updated first name is incorrect"),
                () -> assertEquals(student.getLastName(), updated.getLastName(), "Lstudent updated last name is incorrect"),
                () -> assertEquals(student.getCredits(), updated.getCredits(), "student updated credits is incorrect"),
                () -> assertEquals(student.getCompletedSubjects(), updated.getCompletedSubjects(), "student updated completed subjects is incorrect")
        );
    }

    private void delete(Student student) {
        dbTesting.inTransaction(() -> dao.delete(student));
        assertNull(dbTesting.inTransaction(() -> dao.get(student.getId())), "Student não foi removido");
    }
}