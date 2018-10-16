package br.ufal.ic.academico.subject;

import br.ufal.ic.academico.api.student.*;
import br.ufal.ic.academico.api.teacher.*;
import br.ufal.ic.academico.api.course.*;
import br.ufal.ic.academico.api.subject.*;
import br.ufal.ic.academico.api.secretary.*;
import br.ufal.ic.academico.api.department.*;

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
        final Subject d1 = create("Programação 1", "CC001", 80, 0, new ArrayList<>());
        get(d1);

        d1.setTeacher(new Teacher("Rodrigo", "Paes"));
        d1.setCredits(60);
        d1.setRequiredCredits(100);
        List<String> preRequisites = new ArrayList<>();
        preRequisites.add("CC002");
        preRequisites.add("CC003");
        d1.setRequiredSubjects(preRequisites);
        update(d1);

        delete(d1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Subject1 não foi removido da listagem total de Subjects");

        final Subject d2 = create("Programação 2", "CC002", 0, 0, new ArrayList<>());
        get(d2);
        final Subject d3 = create("Teste de Software", "CC003", 0, 0, new ArrayList<>());
        get(d3);

        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todas as novas Subjects estão aparecendo na listagem total de Subjects");

        delete(d2);

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Subject2 não foi removido da listagem total de Subjects");
        assertEquals(d3.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "Subject3 não está na listagem total de Subjects");
    }

    private Subject create(String name, String code, Integer credits, Integer requiredCredits, List<String> requiredSubjects) {
        final Subject subject = new Subject(name, code, credits, requiredCredits, requiredSubjects);

        final Subject saved = dbTesting.inTransaction(() -> dao.persist(subject));
        assertNull(dbTesting.inTransaction(() -> dao.getCourse(subject)), "Subject foi associada a um Course ao ser criada");
        assertNull(dbTesting.inTransaction(() -> dao.getSecretary(subject)), "Subject foi associada a uma Secretary ao ser criada");
        assertNotNull(saved, "Falhou ao salvar uma nova Subject");
        assertNotNull(saved.getId(), "Subject não recebeu um id ao ser criada");
        assertEquals(code, saved.getCode(), "Code da Subject não corresponde com o informado");
        assertEquals(name, saved.getName(), "Name da Subject não corresponde com o informado");
        assertEquals(credits, saved.getCredits(), "Credits não corresponde com o informado");
        assertEquals(requiredCredits, saved.getRequiredCredits(), "Required Credits não corresponde com o informado");
        assertEquals(requiredSubjects.size(), saved.getRequiredSubjects().size(), "Pré-requisitos foram associados incorretamente");
        assertNull(saved.getTeacher(), "Um teacher foi associado à nova Subject");
        assertEquals(new ArrayList<>(), saved.getStudents(), "Aluno(s) foi(ram) associado(s) à nova Subject");

        return subject;
    }

    private void get(Subject subject) {
        Subject recovered = dbTesting.inTransaction(() -> dao.get(subject.getId()));

        assertEquals(subject.getId(), recovered.getId(), "ID da Subject recuperada não confere com o informado");
        assertEquals(subject.getName(), recovered.getName(), "Name da Subject recuperada não confere com o informada");
        assertEquals(subject.getCode(), recovered.getCode(), "Code da Subject recuperada não confere com o informado");
        assertEquals(subject.getCredits(), recovered.getCredits(), "Credits da Subject recuperada não confere com o informado");
        assertEquals(subject.getRequiredCredits(), recovered.getRequiredCredits(),
                "Required Credits da Subject recuperada não confere com o informado");
        assertEquals(subject.getRequiredSubjects().size(), recovered.getRequiredSubjects().size(),
                "Quantidade de Required Subjects da Subject recuperada não confere com a informada");
    }

    private void update(Subject subject) {
        final Subject updated = dbTesting.inTransaction(() -> dao.persist(subject));

        assertEquals(subject.getId(), updated.getId(), "Ao ser atualizada, Subject teve seu ID alterado");
        assertEquals(subject.getName(), updated.getName(), "Name da Subject não foi alterado corretamente");
        assertEquals(subject.getCode(), updated.getCode(), "Code da Subject não foi alterado corretamente");
        if (subject.getTeacher() != null) {
            assertNotNull(updated.getTeacher(), "Nenhum Teacher foi associado à Subject");
            assertEquals(subject.getTeacher().getId(), updated.getTeacher().getId(), "Teacher correto não foi associado à Subject");
        } else {
            assertNull(updated.getTeacher(), "Teacher foi associado à Subject ao atualizá-la");
        }
        assertEquals(subject.getStudents().size(), updated.getStudents().size(), "Lista de Students foi alterada incorretamente");
        assertEquals(subject.getCredits(), updated.getCredits(), "O valor de credits da Subject não foi atualizado corretamente");
        assertEquals(subject.getRequiredCredits(), updated.getRequiredCredits(), "Required credits não foi atualizado corretamente");
        assertEquals(subject.getRequiredSubjects().size(), updated.getRequiredSubjects().size(),
                "Pré-requisitos não foram atualizados corretamente");
    }

    private void delete(Subject subject) {
        dbTesting.inTransaction(() -> dao.delete(subject));
        assertNull(dbTesting.inTransaction(() -> dao.get(subject.getId())), "Subject não foi removida");
    }
}