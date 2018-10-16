package br.ufal.ic.academico.student;

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

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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
        final Student s1 = create("first name", "last name");
        get(s1);

        for (int i = 0; i < 50; i++) {
            Integer credits = new Random().nextInt();
            s1.setCredits(credits);
            update(s1);
        }
        s1.setLastName("las");
        s1.setLastName("Vassalo");
        update(s1);

        delete(s1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Student1 não foi removido da listagem de todos os Students");

        final Student s2 = create("Lucas", "Raggi");
        get(s2);
        final Student s3 = create("Gabriel", "Barbosa");
        get(s3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Students estão aparecendo na listagem total de Students");

        delete(s2);

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Student2 não foi removido da listagem de todos os Student");
        assertEquals(s3.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "Student3 não está na listagem de todos os Students restantes");
    }

    private Student create(String firstName, String lastName) {
        final Student student = new Student(firstName, lastName);
        final Student saved = dbTesting.inTransaction(() -> dao.persist(student));

        assertNotNull(saved, "Falhou ao salvar um novo Student");
        assertNotNull(saved.getId(), "Student não recebeu um id ao ser criado");
        assertEquals(student.getFirstName(), saved.getFirstName(), "First name do Student não corresponde com o informado");
        assertEquals(student.getLastName(), saved.getLastName(), "Last name do Student não corresponde com o informado");
        assertEquals(new Integer(0), saved.getCredits(), "Student foi cadastro com Credits diferente de 0");
        assertNull(saved.getCourse(), "Student recebeu um curso ao ser criado");
        assertEquals(new ArrayList<>(), saved.getCompletedSubjects(),
                "Student recebeu uma lista de matérias concluídas ao ser criado");
        assertNull(dbTesting.inTransaction(() -> dao.getDepartment(saved)), "Student foi vinculado à um Department ao ser criado");
        assertNull(dbTesting.inTransaction(() -> dao.getSecretary(saved)), "Student foi vinculado à uma Secretary ao ser criado");

        return student;
    }

    private void get(Student student) {
        Student recovered = dbTesting.inTransaction(() -> dao.get(student.getId()));

        assertEquals(student.getId(), recovered.getId(), "ID do Student recuperado não confere com o informado");
        assertEquals(student.getFirstName(), recovered.getFirstName(), "First Name do Student recuperado não confere com o informado");
        assertEquals(student.getLastName(), recovered.getLastName(), "Last Name do Student recuperado não confere com o informado");
        assertEquals(student.getCredits(), recovered.getCredits(), "Credits do Student recuperado não confere com o informado");
        assertEquals(student.getCompletedSubjects().size(), recovered.getCompletedSubjects().size(),
                "Quantidade de Completed Subjects do Student recuperado não confere com a informada");
    }

    private void update(Student student) {
        final Student updated = dbTesting.inTransaction(() -> dao.persist(student));
        assertEquals(student.getCredits(), updated.getCredits(), "Créditos não foram atualizados corretamente");
        assertEquals(student.getFirstName(), updated.getFirstName(), "First name não foi atualizado corretamente");
        assertEquals(student.getLastName(), updated.getLastName(), "Last name não foi atualizado corretamente");
    }

    private void delete(Student student) {
        dbTesting.inTransaction(() -> dao.delete(student));
        assertNull(dbTesting.inTransaction(() -> dao.get(student.getId())), "Student não foi removido");
    }
}