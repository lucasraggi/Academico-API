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

import static org.junit.jupiter.api.Assertions.*;

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
        Department department1 = create("test name");
        update(department1);
        delete(department1);

        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "department was not removed");

        Department department2 = create("test name 2");

        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "department not appearing on department get all");
        assertEquals(department2.getId(), dbTesting.inTransaction(dao::getAll).get(0).getId(),
                "department could not be reached");
    }

    private Department create(String name) {
        Department department = new Department(name);
        Department departmentDB = dbTesting.inTransaction(() -> dao.persist(department));

        assertAll(
                () -> assertNotNull(departmentDB, "failed to save department"),
                () -> assertNotNull(departmentDB.getId(), "department did not receiva an id"),
                () -> assertEquals(department.getName(), departmentDB.getName(), "department name is different from the predicted"),
                () -> assertNull(departmentDB.getGraduate(), "department received an graduation secretary when it shouldnt"),
                () -> assertNull(departmentDB.getPostgraduate(), "department received an graduation secretary when it shouldnt")
        );

        return department;
    }

    private void update(Department department) {
        Department d = dbTesting.inTransaction(() -> dao.persist(department));

        assertAll(
                () -> assertEquals(department.getId(), d.getId(), "department id is incorrect"),
                () -> assertEquals(department.getName(), d.getName(), "department name is incorrect"),
                () -> assertEquals(department.getGraduate(), d.getGraduate(), "graduation secretary is incorrect"),
                () -> assertEquals(department.getPostgraduate(), d.getPostgraduate(), "post graduation secretary is incorrect")
        );
    }

    private void delete(Department department) {
        dbTesting.inTransaction(() -> dao.delete(department));
        assertNull(dbTesting.inTransaction(() -> dao.get(department.getId())), "Department não foi removido");
    }
}