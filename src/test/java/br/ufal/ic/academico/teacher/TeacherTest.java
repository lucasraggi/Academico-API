package br.ufal.ic.academico.teacher;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.department.*;
import br.ufal.ic.academico.api.secretary.*;
import br.ufal.ic.academico.api.subject.*;

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
        final Teacher t1 = create("Willy", "Carvalho Tiengo");

        get(t1);

        update(t1, "Will", "Tiengo");

        delete(t1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Teacher1 não foi removido da listagem de todos os Teachers");

        final Teacher t2 = create("Rodrigo", "Paes");
        get(t2);
        final Teacher t3 = create("Márcio", "Ribeiro");
        get(t3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Teachers estão aparecendo na listagem total de Teachers");

        delete(t2);

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Teacher2 não foi removido da listagem de todos os Teachers");
        assertEquals(t3.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "Teacher3 não está na listagem de todos os Teachers restantes");
    }

    private Teacher create(String firstName, String lastName) {
        final Teacher teacher = new Teacher(firstName, lastName);
        final Teacher savedT1 = dbTesting.inTransaction(() -> dao.persist(teacher));

        assertNotNull(savedT1, "Falhou ao salvar um novo Teacher");
        assertNotNull(savedT1.getId(), "Teacher não recebeu um id ao ser criado");
        assertEquals(teacher.getFirstName(), savedT1.getFirstName(), "First name do Teacher não corresponde com o informado");
        assertEquals(teacher.getLastName(), savedT1.getLastName(), "Last name do Teacher não corresponde com o informado");

        return teacher;
    }

    private void get(Teacher teacher) {
        Teacher recovered = dbTesting.inTransaction(() -> dao.get(teacher.getId()));

        assertEquals(teacher.getId(), recovered.getId(), "ID do Teacher recuperado não confere com o informado");
        assertEquals(teacher.getFirstName(), recovered.getFirstName(), "First Name do Teacher recuperado não confere com o informado");
        assertEquals(teacher.getLastName(), recovered.getLastName(), "Last Name do Teacher recuperado não confere com o informado");
    }

    private void update(Teacher teacher, String newFirstName, String newLastName) {
        teacher.setFirstName("Will");
        teacher.setLastName("Tiengo");
        final Teacher updated = dbTesting.inTransaction(() -> dao.persist(teacher));
        assertEquals(teacher.getFirstName(), updated.getFirstName(), "First name não foi atualizado corretamente");
        assertEquals(teacher.getLastName(), updated.getLastName(), "Last name não foi atualizado corretamente");
    }

    private void delete(Teacher teacher) {
        dbTesting.inTransaction(() -> dao.delete(teacher));
        assertNull(dbTesting.inTransaction(() -> dao.get(teacher.getId())), "Teacher não foi removido");
    }
}