package br.ufal.ic.academico.subject;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.subject.*;
import br.ufal.ic.academico.api.secretary.*;

import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(DropwizardExtensionsSupport.class)
class SubjectTest {
    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    private SubjectDAO dao = new SubjectDAO(dbTesting.getSessionFactory());

    @Test
    void subjectCRUD() {
        final Subject subject1 = create("subject name", "code", 80, 0, new ArrayList<>());
        get(subject1);
        update(subject1);
        delete(subject1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "subject was not removed from get all");

        final Subject subject2 = create("subject name", "code", 0, 0, new ArrayList<>());
        get(subject2);
        final Subject subject3 = create("subject name2", "code2", 0, 0, new ArrayList<>());
        get(subject3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "subject was not found in get all");

    }

    private Subject create(String name, String code, Integer credits, Integer requiredCredits, List<String> requiredSubjects) {
        final Subject subject = new Subject(name, code, credits, requiredCredits, requiredSubjects);
        final Subject saved = dbTesting.inTransaction(() -> dao.persist(subject));

        assertNotNull(saved.getId(), "subject id is null");
        assertEquals(code, saved.getCode(), "subject code is incorrect");
        assertEquals(name, saved.getName(), "subject name is incorrect");
        assertEquals(credits, saved.getCredits(), "subject credits is incorrect");
        assertEquals(requiredCredits, saved.getRequiredCredits(), "subject required credits is incorrect");
        assertEquals(requiredSubjects, saved.getRequiredSubjects(), "subject required subjects is incorrect");

        return subject;
    }

    private void get(Subject subject) {
        Subject retrieved = dbTesting.inTransaction(() -> dao.get(subject.getId()));

        assertEquals(subject.getId(), retrieved.getId(), "retrieved subject id is incorrect");
        assertEquals(subject.getCode(), retrieved.getCode(), "retrieved subject code is incorrect");
        assertEquals(subject.getName(), retrieved.getName(), "retrieved subject name is incorrect");
        assertEquals(subject.getCredits(), retrieved.getCredits(), "retrieved subject credits is incorrect");
        assertEquals(subject.getRequiredCredits(), retrieved.getRequiredCredits(), "retrieved subject required credits is incorrect");
        assertEquals(subject.getRequiredSubjects(), retrieved.getRequiredSubjects(), "retrieved subject required subjects is incorrect");
    }

    private void update(Subject subject) {
        final Subject updated = dbTesting.inTransaction(() -> dao.persist(subject));

        assertEquals(subject.getId(), updated.getId(), "updated subject id is incorrect");
        assertEquals(subject.getName(), updated.getName(), "updated subject name is incorrect");
        assertEquals(subject.getCode(), updated.getCode(), "updated subject code is incorrect");
        assertEquals(subject.getStudents(), updated.getStudents(), "updated students list is incorrect");
        assertEquals(subject.getCredits(), updated.getCredits(), "updated subject credits is incorrect");
        assertEquals(subject.getRequiredCredits(), updated.getRequiredCredits(), "updated subject required credits is incorrect");
        assertEquals(subject.getRequiredSubjects(), updated.getRequiredSubjects(), "updated subject required subjects is incorrect" );
    }

    private void delete(Subject subject) {
        dbTesting.inTransaction(() -> dao.delete(subject));
        assertNull(dbTesting.inTransaction(() -> dao.get(subject.getId())), "Subject não foi removida");
    }
}