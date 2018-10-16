package br.ufal.ic.academico.department;

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
class DepartmentTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Department.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    private DepartmentDAO dao = new DepartmentDAO(dbTesting.getSessionFactory());

    @Test
    void departmentCRUD() {
        final Department d1 = create("test name");
        update(d1);
        delete(d1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "department was not removed");

        final Department d2 = create("test name 2");

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "department not appearing on department get all");
        assertEquals(d2.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "department could not be reached");
    }

    private Department create(String name) {
        final Department department = new Department(name);
        final Department saved = dbTesting.inTransaction(() -> dao.persist(department));

        assertNotNull(saved, "failed to save department");
        assertNotNull(saved.getId(), "department did not receiva an id");
        assertEquals(department.getName(), saved.getName(), "department name is different from the predicted");
        assertNull(saved.getGraduate(), "department received an graduation secretary when it shouldnt");
        assertNull(saved.getPostgraduate(), "department received an graduation secretary when it shouldnt");

        return department;
    }

    private void update(Department department) {
        final Department d = dbTesting.inTransaction(() -> dao.persist(department));

        assertEquals(department.getId(), d.getId(), "department id is incorrect");
        assertEquals(department.getName(), d.getName(), "department name is incorrect");
        assertEquals(department.getGraduate(), d.getGraduate(), "graduation secretary is incorrect");
        assertEquals(department.getPostgraduate(), d.getPostgraduate(), "post graduation secretary is incorrect");
    }

    private void delete(Department department) {
        dbTesting.inTransaction(() -> dao.delete(department));
        assertNull(dbTesting.inTransaction(() -> dao.get(department.getId())), "Department não foi removido");
    }
}