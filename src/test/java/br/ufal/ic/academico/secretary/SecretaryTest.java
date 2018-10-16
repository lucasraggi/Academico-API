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
        get(s1);

        s1.addCourse(new Course());
        update(s1);

        delete(s1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Secretary1 não foi removida da listagem total de Secretaries");

        final Secretary s2 = create("POST-GRADUATION");
        get(s2);
        final Secretary s3 = create("GRADUATION");
        get(s3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todas as novas Secretaries estão aparecendo na listagem total de Secretaries");

        delete(s2);

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Secretary2 não foi removida da listagem total de Secretaries");
        assertEquals(s3.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "Secretary3 não está na listagem de todas as Secretaries restantes");
    }

    private Secretary create(String type) {
        final Secretary secretary = new Secretary(type);
        final Secretary saved = dbTesting.inTransaction(() -> dao.persist(secretary));

        assertNotNull(saved, "Falhou ao salvar uma nova Secretary");
        assertNotNull(saved.getId(), "Secretary não recebeu um id ao ser criada");
        assertEquals(type, saved.getType(), "Tipo da Secretary não corresponde com o informado (GRADUATION)");
        assertEquals(0, saved.getCourses().size(), "Secretary foi criada com Course(s) associado(s)");

        return secretary;
    }

    private void get(Secretary secretary) {
        Secretary recovered = dbTesting.inTransaction(() -> dao.get(secretary.getId()));

        assertEquals(secretary.getId(), recovered.getId(), "ID da Secretary recuperada não confere com o informado");
        assertEquals(secretary.getType(), recovered.getType(), "Type da Secretary recuperada não confere com o informado");
        assertEquals(secretary.getCourses().size(), recovered.getCourses().size(),
                "Quantidade de Courses da Secretary recuperada não confere com a informada");
    }

    private void update(Secretary secretary) {
        final Secretary updated = dbTesting.inTransaction(() -> dao.persist(secretary));
        assertEquals(secretary.getType(), updated.getType(), "Type da Secretary foi alterado");
        assertEquals(secretary.getCourses().size(), updated.getCourses().size(), "Courses associados não foram salvos corretamente");
    }

    private void delete(Secretary secretary) {
        dbTesting.inTransaction(() -> dao.delete(secretary));
        assertNull(dbTesting.inTransaction(() -> dao.get(secretary.getId())), "Secretary não foi removida");
    }
}