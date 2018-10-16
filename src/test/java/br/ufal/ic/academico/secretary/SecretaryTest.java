package br.ufal.ic.academico.secretary;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.secretary.*;
import br.ufal.ic.academico.api.department.*;
import br.ufal.ic.academico.api.subject.*;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        final Secretary s1 = create("GRADUATION");
        delete(s1);

        assertEquals(s1.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(), "secretary is not on database");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(), "secretary wasnt removed from database");

        final Secretary s2 = create("POST-GRADUATION");
        get(s2);
        final Secretary s3 = create("GRADUATION");
        get(s3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "secretary's wasn created correctly");

    }

    private Secretary create(String type) {
        final Secretary secretary = new Secretary(type);
        final Secretary saved = dbTesting.inTransaction(() -> dao.persist(secretary));

        assertNotNull(saved, "secretary was not saved");
        assertNotNull(saved.getId(), "secretary did not received an id");
        assertEquals(type, saved.getType(), "secretary types is incorrect");

        return secretary;
    }

    private void update(Secretary secretary) {
        final Secretary updated = dbTesting.inTransaction(() -> dao.persist(secretary));
        assertEquals(secretary.getId(), recovered.getId(), "secretary id is incorrect");
        assertEquals(secretary.getType(), updated.getType(), "secretary type is incorrect");
        assertEquals(secretary.getCourses(), updated.getCourses(), "secretary courses are incorrect");
    }

    private void delete(Secretary secretary) {
        dbTesting.inTransaction(() -> dao.delete(secretary));
        assertNull(dbTesting.inTransaction(() -> dao.get(secretary.getId())), "Secretary não foi removida");
    }
}