package br.ufal.ic.academico.secretary;

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
class SecretaryTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    private SecretaryDAO dao = new SecretaryDAO(dbTesting.getSessionFactory());

    @Test
    void secretaryCRUD() {
        Secretary secretary1 = create("GRADUATION");
        update(secretary1);
        assertEquals(secretary1.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(), "secretary is not on database");
        delete(secretary1);
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(), "secretary wasnt removed from database");

        Secretary secretary2 = create("POST-GRADUATION");
        Secretary secretary3 = create("GRADUATION");

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "secretary's wasn created correctly");

    }

    private Secretary create(String type) {
        Secretary secretary = new Secretary(type);
        Secretary secretaryDB = dbTesting.inTransaction(() -> dao.persist(secretary));

        assertAll(
                () -> assertNotNull(secretaryDB, "secretary was not secretaryDB"),
                () -> assertNotNull(secretaryDB.getId(), "secretary did not received an id"),
                () -> assertEquals(type, secretaryDB.getType(), "secretary types is incorrect")
        );

        return secretary;
    }

    private void update(Secretary secretary) {
        Secretary updated = dbTesting.inTransaction(() -> dao.persist(secretary));

        assertAll(
                () -> assertEquals(secretary.getId(), updated.getId(), "secretary id is incorrect"),
                () -> assertEquals(secretary.getType(), updated.getType(), "secretary type is incorrect"),
                () -> assertEquals(secretary.getCourses(), updated.getCourses(), "secretary courses are incorrect")
        );
    }

    private void delete(Secretary secretary) {
        dbTesting.inTransaction(() -> dao.delete(secretary));
        assertNull(dbTesting.inTransaction(() -> dao.get(secretary.getId())), "Secretary não foi removida");
    }
}