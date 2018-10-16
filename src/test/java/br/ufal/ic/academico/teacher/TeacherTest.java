package br.ufal.ic.academico.teacher;

import br.ufal.ic.academico.api.teacher.*;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DropwizardExtensionsSupport.class)
class TeacherTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Teacher.class)
            .build();

    private TeacherDAO dao = new TeacherDAO(dbTesting.getSessionFactory());

    @Test
    void teacherCRUD() {
        final Teacher teacher1 = create("first name", "last name");

        get(teacher1);
        update(teacher1, "first", "last");
        delete(teacher1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "teacher was not removed");

        final Teacher teacher2 = create("first name 1", "last name 1");
        get(teacher2);
        final Teacher teacher3 = create("first name 2", "last name 2");
        get(teacher3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "teacher is not appearing in get all");
    }

    private Teacher create(String firstName, String lastName) {
        final Teacher teacher = new Teacher(firstName, lastName);
        final Teacher savedT1 = dbTesting.inTransaction(() -> dao.persist(teacher));

        assertNotNull(savedT1, "saved teacher is null");
        assertNotNull(savedT1.getId(), "saved teacher id is null");
        assertEquals(teacher.getFirstName(), savedT1.getFirstName(), "teacher first name is incorrect");
        assertEquals(teacher.getLastName(), savedT1.getLastName(), "teacher last name is incorrect");

        return teacher;
    }

    private void get(Teacher teacher) {
        Teacher retrieved = dbTesting.inTransaction(() -> dao.get(teacher.getId()));

        assertEquals(teacher.getId(), retrieved.getId(), "teacher retrieved id is incorrect");
        assertEquals(teacher.getFirstName(), retrieved.getFirstName(), "teacher retrieved first name is incorrect");
        assertEquals(teacher.getLastName(), retrieved.getLastName(), "teacher retrieved last name is incorrect");
    }

    private void update(Teacher teacher, String newFirstName, String newLastName) {
        final Teacher updated = dbTesting.inTransaction(() -> dao.persist(teacher));
        assertEquals(teacher.getId(), updated.getId(), "teacher updated id is incorrect");
        assertEquals(teacher.getFirstName(), updated.getFirstName(), "teacher updated first name is incorrect");
        assertEquals(teacher.getLastName(), updated.getLastName(), "teacher updated last name is incorrect");
    }

    private void delete(Teacher teacher) {
        dbTesting.inTransaction(() -> dao.delete(teacher));
        assertNull(dbTesting.inTransaction(() -> dao.get(teacher.getId())), "Teacher não foi removido");
    }
}