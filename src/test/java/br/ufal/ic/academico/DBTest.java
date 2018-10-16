package br.ufal.ic.academico;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDAO;
import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDAO;
import br.ufal.ic.academico.api.teacher.Teacher;
import br.ufal.ic.academico.api.teacher.TeacherDAO;
import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import io.dropwizard.testing.junit5.DAOTestExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExtendWith(DropwizardExtensionsSupport.class)
class DBTest {

    private DAOTestExtension dbTesting = DAOTestExtension.newBuilder()
            .addEntityClass(Student.class)
            .addEntityClass(Teacher.class)
            .addEntityClass(Department.class)
            .addEntityClass(Secretary.class)
            .addEntityClass(Course.class)
            .addEntityClass(Subject.class)
            .build();

    @Test
    void studentCRUD() {
        StudentDAO dao = new StudentDAO(dbTesting.getSessionFactory());

        final Student s1 = new Student("Daniel", "Humberto Cavalcante Vassalo");
        final Student savedS1 = dbTesting.inTransaction(() -> dao.persist(s1));

        assertNotNull(savedS1, "Falhou ao salvar um novo student");
        assertNotNull(savedS1.getId(), "student não recebeu um id ao ser criado");
        assertEquals(s1.getFirstName(), savedS1.getFirstName(), "First name do student não corresponde com o informado");
        assertEquals(s1.getLastName(), savedS1.getLastName(), "Last name do student não corresponde com o informado");
        assertEquals(new Integer(0), savedS1.getCredits(), "student foi cadastro com 'credits' diferente de 0");
        assertNull(savedS1.getCourse(), "student recebeu um curso ao ser criado");
        assertEquals(new ArrayList<>(), savedS1.getCompletedSubjects(),
                "student recebeu uma lista de matérias concluídas ao ser criado");
        assertNull(dbTesting.inTransaction(() -> dao.getDepartment(savedS1)), "student foi vinculado à um Department");
        assertNull(dbTesting.inTransaction(() -> dao.getSecretary(savedS1)), "student foi vinculado à uma Secretary");

        for (int i = 0; i < 50; i++) {
            Integer credits = new Random().nextInt();
            s1.setCredits(credits);
            final Student updatedS1 = dbTesting.inTransaction(() -> dao.persist(s1));
            assertEquals(credits, updatedS1.getCredits(), "Créditos não foram atualizados corretamente");
        }
        s1.setLastName("Dan");
        s1.setLastName("Vassalo");
        final Student updatedS1 = dbTesting.inTransaction(() -> dao.persist(s1));
        assertEquals(s1.getFirstName(), updatedS1.getFirstName(), "First name não foi atualizado corretamente");
        assertEquals(s1.getLastName(), updatedS1.getLastName(), "Last name não foi atualizado corretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedS1));
        assertNull(dbTesting.inTransaction(() -> dao.get(s1.getId())), "student não foi removido");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "student não foi removido da listagem de todos os student");

        final Student s2 = new Student("Lucas", "Raggi");
        final Student s3 = new Student("Gabriel", "Barbosa");
        final Student savedS2 = dbTesting.inTransaction(() -> dao.persist(s2));
        final Student savedS3 = dbTesting.inTransaction(() -> dao.persist(s3));

        assertNotNull(savedS2, "Falhou ao salvar um segundo novo student");
        assertNotNull(savedS3, "Falhou ao salvar um terceiro novo student");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Students estão aparecendo na listagem total de Students");
        dbTesting.inTransaction(() -> dao.delete(s2));
        assertNull(dbTesting.inTransaction(() -> dao.get(s2.getId())), "student não foi removido");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "student não foi removido da listagem de todos os Students");
    }

    @Test
    void teacherCRUD() {
        TeacherDAO dao = new TeacherDAO(dbTesting.getSessionFactory());

        final Teacher t1 = new Teacher("Willy", "Carvalho Tiengo");
        final Teacher savedT1 = dbTesting.inTransaction(() -> dao.persist(t1));

        assertNotNull(savedT1, "Falhou ao salvar um novo teacher");
        assertNotNull(savedT1.getId(), "teacher não recebeu um id ao ser criado");
        assertEquals(t1.getFirstName(), savedT1.getFirstName(), "First name do teacher não corresponde com o informado");
        assertEquals(t1.getLastName(), savedT1.getLastName(), "Last name do teacher não corresponde com o informado");

        t1.setLastName("Will");
        t1.setLastName("Tiengo");
        final Teacher updatedT1 = dbTesting.inTransaction(() -> dao.persist(t1));
        assertEquals(t1.getFirstName(), updatedT1.getFirstName(), "First name não foi atualizado corretamente");
        assertEquals(t1.getLastName(), updatedT1.getLastName(), "Last name não foi atualizado corretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedT1));
        assertNull(dbTesting.inTransaction(() -> dao.get(t1.getId())), "teacher não foi removido");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "teacher não foi removido da listagem de todos os Teachers");

        final Teacher t2 = new Teacher("Rodrigo", "Paes");
        final Teacher t3 = new Teacher("Márcio", "Ribeiro");
        final Teacher savedT2 = dbTesting.inTransaction(() -> dao.persist(t2));
        final Teacher savedT3 = dbTesting.inTransaction(() -> dao.persist(t3));

        assertNotNull(savedT2, "Falhou ao salvar um segundo novo teacher");
        assertNotNull(savedT3, "Falhou ao salvar um terceiro novo teacher");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Teachers estão aparecendo na listagem total de Teachers");
        dbTesting.inTransaction(() -> dao.delete(t2));
        assertNull(dbTesting.inTransaction(() -> dao.get(t2.getId())), "teacher não foi removido");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "teacher não foi removido da listagem de todos os Teachers");
    }

    @Test
    void departmentCRUD() {
        DepartmentDAO dao = new DepartmentDAO(dbTesting.getSessionFactory());

        final Department d1 = new Department("IC");
        final Department savedD1 = dbTesting.inTransaction(() -> dao.persist(d1));

        assertNotNull(savedD1, "Falhou ao salvar um novo Department");
        assertNotNull(savedD1.getId(), "Department não recebeu um id ao ser criado");
        assertEquals(d1.getName(), savedD1.getName(), "Name do Department não corresponde com o informado");
        assertNull(savedD1.getGraduate(), "Department recebeu uma secretaria de graduação ao ser criado");
        assertNull(savedD1.getPostgraduate(), "Department recebeu uma secretaria de pós graduação ao ser criado");

        d1.setName("FDA");
        d1.setGraduate(new Secretary());
        d1.setPostgraduate(new Secretary());
        final Department updatedD1 = dbTesting.inTransaction(() -> dao.persist(d1));
        assertEquals(d1.getName(), updatedD1.getName(), "Name do Department não foi atualizado corretamente");
        assertEquals(d1.getGraduate().getId(), updatedD1.getGraduate().getId(),
                "Secretaria de graduação associada incorretamente");
        assertEquals(d1.getPostgraduate().getId(), updatedD1.getPostgraduate().getId(),
                "Secretaria de pós graduação associada incorretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedD1));
        assertNull(dbTesting.inTransaction(() -> dao.get(d1.getId())), "Department não foi removido");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Department não foi removido da listagem total de Department");

        final Department d2 = new Department("ICBS");
        final Department d3 = new Department("COS");
        final Department savedD2 = dbTesting.inTransaction(() -> dao.persist(d2));
        final Department savedD3 = dbTesting.inTransaction(() -> dao.persist(d3));

        assertNotNull(savedD2, "Falhou ao salvar um segundo novo Department");
        assertNotNull(savedD3, "Falhou ao salvar um terceiro novo Department");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Departments estão aparecendo na listagem total de Departments");
        dbTesting.inTransaction(() -> dao.delete(d2));
        assertNull(dbTesting.inTransaction(() -> dao.get(d2.getId())), "Department não foi removido");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Department não foi removido da listagem total de Departments");
    }

    @Test
    void secretaryCRUD() {
        SecretaryDAO dao = new SecretaryDAO(dbTesting.getSessionFactory());

        final Secretary s1 = new Secretary("GRADUATION");
        final Secretary savedS1 = dbTesting.inTransaction(() -> dao.persist(s1));

        assertNotNull(savedS1, "Falhou ao salvar uma nova Secretary");
        assertNotNull(savedS1.getId(), "Secretary não recebeu um id ao ser criada");
        assertEquals("GRADUATION", savedS1.getType(), "Tipo da Secretary não corresponde com o informado (GRADUATION)");
        assertEquals(0, savedS1.getCourses().size(), "Secretary foi criada com Course(s) associado(s)");

        s1.addCourse(new Course());
        final Secretary updatedS1 = dbTesting.inTransaction(() -> dao.persist(s1));
        assertEquals("GRADUATION", updatedS1.getType(), "Tipo da Secretary foi alterado");
        assertEquals(1, updatedS1.getCourses().size(), "Curso associado não foi salvo corretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedS1));
        assertNull(dbTesting.inTransaction(() -> dao.get(s1.getId())), "Secretary não foi removida");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Secretary não foi removida da listagem total de Secretaries");

        final Secretary s2 = new Secretary("POST-GRADUATION");
        final Secretary s3 = new Secretary("GRADUATION");
        final Secretary savedS2 = dbTesting.inTransaction(() -> dao.persist(s2));
        final Secretary savedS3 = dbTesting.inTransaction(() -> dao.persist(s3));

        assertNotNull(savedS2, "Falhou ao salvar uma segunda nova Secretary");
        assertNotNull(savedS3, "Falhou ao salvar uma terceira nova Secretary");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todas as novas Secretaries estão aparecendo na listagem total de Secretaries");
        dbTesting.inTransaction(() -> dao.delete(s2));
        assertNull(dbTesting.inTransaction(() -> dao.get(s2.getId())), "Secretary não foi removida");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Secretary não foi removida da listagem total de Secretaries");
    }

    @Test
    void courseCRUD() {
        CourseDAO dao = new CourseDAO(dbTesting.getSessionFactory());

        final Course c1 = new Course("Ciência da Computação");
        final Course savedC1 = dbTesting.inTransaction(() -> dao.persist(c1));

        assertNotNull(savedC1, "Falhou ao salvar um novo Course");
        assertNotNull(savedC1.getId(), "Course não recebeu um id ao ser criado");
        assertEquals("Ciência da Computação", savedC1.getName(),
                "Name do Course não corresponde com o informado");
        assertEquals(0, savedC1.getSubjects().size(), "Course foi criado com Subject(s) associada(s)");
        assertNull(dbTesting.inTransaction(() -> dao.getSecretary(savedC1)), "Course foi associado à uma Secretary");

        c1.setName("Engenharia da Computação");
        c1.addSubject(new Subject());
        final Course updatedC1 = dbTesting.inTransaction(() -> dao.persist(c1));
        assertEquals("Engenharia da Computação", updatedC1.getName(), "Name do Course não foi atualizado corretamente");
        assertEquals(1, updatedC1.getSubjects().size(), "Discpline não foi associada corretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedC1));
        assertNull(dbTesting.inTransaction(() -> dao.get(c1.getId())), "Course não foi removido");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Course não foi removido da listagem total de Courses");

        final Course c2 = new Course("Jornalismo");
        final Course c3 = new Course("Direito");
        final Course savedC2 = dbTesting.inTransaction(() -> dao.persist(c2));
        final Course savedC3 = dbTesting.inTransaction(() -> dao.persist(c3));

        assertNotNull(savedC2, "Falhou ao salvar um segundo novo Course");
        assertNotNull(savedC3, "Falhou ao salvar um terceiro novo Course");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todos os novos Courses estão aparecendo na listagem total de Courses");
        dbTesting.inTransaction(() -> dao.delete(c2));
        assertNull(dbTesting.inTransaction(() -> dao.get(c2.getId())), "Course não foi removido");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Course não foi removido da listagem total de Courses");
    }

    @Test
    void subjectCRUD() {
        SubjectDAO dao = new SubjectDAO(dbTesting.getSessionFactory());

        final Subject d1 = new Subject("Programação 1", "CC001", 80, 0, new ArrayList<>());
        final Subject savedD1 = dbTesting.inTransaction(() -> dao.persist(d1));

        assertNull(dbTesting.inTransaction(() -> dao.getCourse(d1)));
        assertNull(dbTesting.inTransaction(() -> dao.getSecretary(d1)));
        assertNotNull(savedD1, "Falhou ao salvar uma nova Subject");
        assertNotNull(savedD1.getId(), "Subject não recebeu um id ao ser criada");
        assertEquals("CC001", savedD1.getCode(), "Code da Subject não corresponde com o informado");
        assertEquals("Programação 1", savedD1.getName(),
                "Name da Subject não corresponde com o informado");
        assertEquals(80, (int) savedD1.getCredits(), "Credits não corresponde com o informado");
        assertEquals(0, (int) savedD1.getRequiredCredits(), "Required Credits não corresponde com o informado");
        assertEquals(new ArrayList<>(), savedD1.getRequiredSubjects(), "Pré-requisitos foram associados incorretamente");
        assertNull(savedD1.getTeacher(), "Um teacher foi associado à nova Subject");
//        assertEquals(new ArrayList<>(), savedD1.getStudents(), "Aluno(s) foi(ram) associado(s) à nova Subject");

        d1.setTeacher(new Teacher("Rodrigo", "Paes"));
        d1.setCredits(60);
        d1.setRequiredCredits(100);
        List<String> preRequisites = new ArrayList<>();
        preRequisites.add("CC002");
        preRequisites.add("CC003");
        d1.setRequiredSubjects(preRequisites);
        final Subject updatedD1 = dbTesting.inTransaction(() -> dao.persist(d1));
        assertNotNull(updatedD1.getTeacher(), "teacher não foi associado à Subject");
 //       assertEquals(new ArrayList<>(), updatedD1.getStudents(), "Lista de alunos foi alterada quando não deveria");
        assertEquals(60, (int) updatedD1.getCredits(), "O valor de credits da Subject não foi atualizado corretamente");
        assertEquals(100, (int) updatedD1.getRequiredCredits(), "Required credits não foi atualizado corretamente");
        assertEquals(2, updatedD1.getRequiredSubjects().size(),
                "Pré-requisitos não foram atualizados corretamente");

        dbTesting.inTransaction(() -> dao.delete(updatedD1));
        assertNull(dbTesting.inTransaction(() -> dao.get(d1.getId())), "Subject não foi removida");
        assertEquals(0, dbTesting.inTransaction(dao::getAll).size(),
                "Subject não foi removido da listagem total de Subjects");

        final Subject d2 = new Subject("Programação 2", "CC002", 0, 0, new ArrayList<>());
        final Subject d3 = new Subject("Teste de Software", "CC003", 0, 0, new ArrayList<>());
        final Subject savedD2 = dbTesting.inTransaction(() -> dao.persist(d2));
        final Subject savedD3 = dbTesting.inTransaction(() -> dao.persist(d3));

        assertNotNull(savedD2, "Falhou ao salvar uma segunda nova Subject");
        assertNotNull(savedD3, "Falhou ao salvar uma terceira nova Subject");
        assertEquals(2, dbTesting.inTransaction(dao::getAll).size(),
                "Nem todas as novas Subjects estão aparecendo na listagem total de Subjects");
        dbTesting.inTransaction(() -> dao.delete(d2));
        assertNull(dbTesting.inTransaction(() -> dao.get(d2.getId())), "Subject não foi removida");
        assertEquals(1, dbTesting.inTransaction(dao::getAll).size(),
                "Subject não foi removido da listagem total de Subjects");
    }

    @Test
    void enrollmentCRUD() {
        StudentDAO studentDAO = new StudentDAO(dbTesting.getSessionFactory());
        TeacherDAO teacherDAO = new TeacherDAO(dbTesting.getSessionFactory());
        DepartmentDAO departmentDAO = new DepartmentDAO(dbTesting.getSessionFactory());
        SecretaryDAO secretaryDAO = new SecretaryDAO(dbTesting.getSessionFactory());
        CourseDAO courseDAO = new CourseDAO(dbTesting.getSessionFactory());
        SubjectDAO subjectDAO = new SubjectDAO(dbTesting.getSessionFactory());

        // Students
        Student stdntNewGrad = new Student("Daniel", "Vassalo");
        Student savedStdntNewGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewGrad));

        Student stdntOldGrad = new Student("Gabriel", "Barbosa");
        stdntOldGrad.setCredits(240);
        Student savedStdntOldGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldGrad));

        Student stdntNewPostGrad = new Student("Romero", "Malaquias");
        Student savedStdntNewPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewPostGrad));

        Student stdntOldPostGrad = new Student("Marcos", "Paulo");
        stdntOldPostGrad.setCredits(300);
        Student savedStdntOldPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldPostGrad));

        // Teachers
        Teacher teacher1 = new Teacher("Willy", "Tiengo");
        dbTesting.inTransaction(() -> teacherDAO.persist(teacher1));

        Teacher teacher2 = new Teacher("Rodrigo", "Paes");
        dbTesting.inTransaction(() -> teacherDAO.persist(teacher2));

        // Subjects
        List<String> prerequisites = new ArrayList<>();
        Subject discipGrad1 = new Subject("Programação 1", "EC001", 80, 0, new ArrayList<>());
        discipGrad1.setTeacher(teacher1);
        Subject savedDiscipGrad1 = dbTesting.inTransaction(() -> subjectDAO.persist(discipGrad1));

        prerequisites.add("EC001");
        Subject discipGrad2 = new Subject("Programação 2", "EC002", 80, 80, prerequisites);
        discipGrad2.setTeacher(teacher2);
        Subject savedDiscipGrad2 = dbTesting.inTransaction(() -> subjectDAO.persist(discipGrad2));

        Subject discipPostGrad1 = new Subject("Projeto e Análise de Algoritmos", "CC101", 80, 0, new ArrayList<>());
        discipPostGrad1.setTeacher(teacher2);
        Subject savedDiscipPostGrad1 = dbTesting.inTransaction(() -> subjectDAO.persist(discipPostGrad1));

        Subject discipGrad3 = new Subject("Direito Constitucional", "DD001", 80, 0, new ArrayList<>());
        discipGrad3.setTeacher(teacher1);
        Subject savedDiscipGrad3 = dbTesting.inTransaction(() -> subjectDAO.persist(discipGrad3));

        Subject discipPostGrad2 = new Subject("Direito Penal", "DD101", 80, 0, new ArrayList<>());
        discipPostGrad2.setTeacher(teacher2);
        Subject savedDiscipPostGrad2 = dbTesting.inTransaction(() -> subjectDAO.persist(discipPostGrad2));

        // Courses
        Course compEngineeringGrad = new Course("Engenharia da Computação");
        compEngineeringGrad.addSubject(discipGrad1);
        compEngineeringGrad.addSubject(discipGrad2);
        Course savedCompEngineeringGrad = dbTesting.inTransaction(() -> courseDAO.persist(compEngineeringGrad));

        Course compSciencePostGrad = new Course("Ciência da Computação");
        compSciencePostGrad.addSubject(discipPostGrad1);
        Course savedCompSciencePostGrad = dbTesting.inTransaction(() -> courseDAO.persist(compSciencePostGrad));

        Course lawGrad = new Course("Direito");
        lawGrad.addSubject(discipGrad3);
        Course savedLawGrad = dbTesting.inTransaction(() -> courseDAO.persist(lawGrad));

        Course lawPostGrad = new Course("Direito");
        lawPostGrad.addSubject(discipPostGrad2);
        Course savedLawPostGrad = dbTesting.inTransaction(() -> courseDAO.persist(lawPostGrad));

        // Secretaries
        Secretary secICgrad = new Secretary("GRADUATION");
        secICgrad.addCourse(compEngineeringGrad);
        Secretary savedSecICGrad = dbTesting.inTransaction(() -> secretaryDAO.persist(secICgrad));

        Secretary secICPostGrad = new Secretary("POST-GRADUATION");
        secICPostGrad.addCourse(compSciencePostGrad);
        Secretary savedSecICPostGrad = dbTesting.inTransaction(() -> secretaryDAO.persist(secICPostGrad));

        Secretary secFDAGrad = new Secretary("GRADUATION");
        secFDAGrad.addCourse(lawGrad);
        Secretary savedSecFDAGrad = dbTesting.inTransaction(() -> secretaryDAO.persist(secFDAGrad));

        Secretary secFDAPostGrad = new Secretary("POST-GRADUATION");
        secFDAPostGrad.addCourse(lawPostGrad);
        Secretary savedSecFDAPostGrad = dbTesting.inTransaction(() -> secretaryDAO.persist(secFDAPostGrad));

        // Departments
        Department IC = new Department("IC");
        IC.setGraduate(secICgrad);
        IC.setPostgraduate(secICPostGrad);
        Department savedIC = dbTesting.inTransaction(() -> departmentDAO.persist(IC));

        Department FDA = new Department("FDA");
        FDA.setGraduate(secFDAGrad);
        FDA.setPostgraduate(secFDAPostGrad);
        Department savedFDA = dbTesting.inTransaction(() -> departmentDAO.persist(FDA));

        // Tests
        assertNotNull(discipGrad1.enroll(stdntNewGrad, null, IC, null, secICgrad));

        assertNull(savedStdntNewGrad.getCourse(), "New student foi associado à um curso na criação");
        stdntNewGrad.setCourse(compEngineeringGrad);
        savedStdntNewGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewGrad));
        assertEquals(compEngineeringGrad.getId(), savedStdntNewGrad.getCourse().getId(),
                "New student não foi associado corretamente ao Course");
        assertEquals(secICgrad.getId(), dbTesting.inTransaction(() -> studentDAO.getSecretary(stdntNewGrad)).getId(),
                "New student não foi associado à Secretary correta");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> studentDAO.getDepartment(stdntNewGrad)).getId(),
                "New student não foi associado ao Department correto");

        assertNull(savedStdntOldGrad.getCourse(), "Old student foi associado à um curso na criação");
        stdntOldGrad.setCourse(compEngineeringGrad);
        savedStdntOldGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldGrad));
        assertEquals(compEngineeringGrad.getId(), savedStdntOldGrad.getCourse().getId(),
                "Old student não foi associado corretamente ao Course");
        assertEquals(secICgrad.getId(), dbTesting.inTransaction(() -> studentDAO.getSecretary(stdntOldGrad)).getId(),
                "Old student não foi associado à Secretary correta");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> studentDAO.getDepartment(stdntOldGrad)).getId(),
                "Old student não foi associado ao Department correto");

        assertNull(savedStdntNewPostGrad.getCourse(), "New Post Gradute student foi associado à um curso na criação");
        stdntNewPostGrad.setCourse(compSciencePostGrad);
        savedStdntNewPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewPostGrad));
        assertEquals(compSciencePostGrad.getId(), savedStdntNewPostGrad.getCourse().getId(),
                "New Post Graduate student não foi associado corretamente ao Course");
        assertEquals(secICPostGrad.getId(), dbTesting.inTransaction(() -> studentDAO.getSecretary(stdntNewPostGrad)).getId(),
                "New Post Graduate student não foi associado à Secretary correta");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> studentDAO.getDepartment(stdntNewPostGrad)).getId(),
                "New Post Graduate student não foi associado ao Department correto");

        assertNull(savedStdntOldPostGrad.getCourse(), "Old Post Graduate student foi associado à um curso na criação");
        stdntOldPostGrad.setCourse(compSciencePostGrad);
        savedStdntOldPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldPostGrad));
        assertEquals(compSciencePostGrad.getId(), savedStdntOldPostGrad.getCourse().getId(),
                "Old Post Graduate student não foi associado corretamente ao Course");
        assertEquals(secICPostGrad.getId(), dbTesting.inTransaction(() -> studentDAO.getSecretary(stdntOldPostGrad)).getId(),
                "Old Post Graduate student não foi associado à Secretary correta");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> studentDAO.getDepartment(stdntOldPostGrad)).getId(),
                "Old Post Graduate student não foi associado ao Department correto");

        assertEquals(compEngineeringGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getCourse(discipGrad1)).getId(),
                "Graduation Subject não foi associada ao Course correto");
        assertEquals(compEngineeringGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getCourse(discipGrad2)).getId(),
                "Graduation Subject não foi associada ao Course correto");
        assertEquals(compSciencePostGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getCourse(discipPostGrad1)).getId(),
                "Post graduation Subject não foi associada ao Course correto");
        assertEquals(lawGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getCourse(discipGrad3)).getId(),
                "Graduation Subject não foi associada ao Course correto");
        assertEquals(lawPostGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getCourse(discipPostGrad2)).getId(),
                "Post graduation Subject não foi associada ao Course correto");

        assertEquals(secICgrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getSecretary(discipGrad1)).getId(),
                "Graduation Subject não foi associada à Secretary correta");
        assertEquals(secICgrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getSecretary(discipGrad2)).getId(),
                "Graduation Subject não foi associada à Secretary correta");
        assertEquals(secICPostGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getSecretary(discipPostGrad1)).getId(),
                "Post graduation Subject não foi associada à Secretary correta");
        assertEquals(secFDAGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getSecretary(discipGrad3)).getId(),
                "Graduation Subject não foi associada à Secretary correta");
        assertEquals(secFDAPostGrad.getId(), dbTesting.inTransaction(() -> subjectDAO.getSecretary(discipPostGrad2)).getId(),
                "Post graduation Subject não foi associada à Secretary correta");

        assertEquals(IC.getId(), dbTesting.inTransaction(() -> subjectDAO.getDepartment(discipGrad1)).getId(),
                "Graduation Subject não foi associada ao Department correto");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> subjectDAO.getDepartment(discipGrad2)).getId(),
                "Graduation Subject não foi associada ao Department correto");
        assertEquals(IC.getId(), dbTesting.inTransaction(() -> subjectDAO.getDepartment(discipPostGrad1)).getId(),
                "Post graduation Subject não foi associada ao Department correto");
        assertEquals(FDA.getId(), dbTesting.inTransaction(() -> subjectDAO.getDepartment(discipGrad3)).getId(),
                "Graduation Subject não foi associada ao Department correto");
        assertEquals(FDA.getId(), dbTesting.inTransaction(() -> subjectDAO.getDepartment(discipPostGrad2)).getId(),
                "Post graduation Subject não foi associada ao Department correto");

        assertEquals(teacher1.getId(), savedDiscipGrad1.getTeacher().getId(),
                "teacher não foi associado à Graduation Subject correta");
        assertEquals(teacher2.getId(), savedDiscipGrad2.getTeacher().getId(),
                "teacher não foi associado à Graduation Subject correta");
        assertEquals(teacher2.getId(), savedDiscipPostGrad1.getTeacher().getId(),
                "teacher não foi associado à Post Graduation Subject correta");
        assertEquals(teacher1.getId(), savedDiscipGrad3.getTeacher().getId(),
                "teacher não foi associado à Graduation Subject correta");
        assertEquals(teacher2.getId(), savedDiscipPostGrad2.getTeacher().getId(),
                "teacher não foi associado à Post Graduation Subject correta");

        assertEquals(2, savedCompEngineeringGrad.getSubjects().size(),
                "Graduation Subjects não foram associadas ao Course corretamente");
        assertEquals(1, savedCompSciencePostGrad.getSubjects().size(),
                "Post Graduation Subjects não foram associadas ao Course corretamente");
        assertEquals(1, savedLawGrad.getSubjects().size(),
                "Graduation Subjects não foram associadas ao Course corretamente");
        assertEquals(1, savedLawPostGrad.getSubjects().size(),
                "Post Graduation Subjects não foram associadas ao Course corretamente");

        assertEquals(secICgrad.getId(), dbTesting.inTransaction(() -> courseDAO.getSecretary(compEngineeringGrad)).getId(),
                "Graduation Course não foi associado à Secretary correta");
        assertEquals(secICPostGrad.getId(), dbTesting.inTransaction(() -> courseDAO.getSecretary(compSciencePostGrad)).getId(),
                "Post Graduation Course não foi associado à Secretary correta");
        assertEquals(secFDAGrad.getId(), dbTesting.inTransaction(() -> courseDAO.getSecretary(lawGrad)).getId(),
                "Graduation Course não foi associado à Secretary correta");
        assertEquals(secFDAPostGrad.getId(), dbTesting.inTransaction(() -> courseDAO.getSecretary(lawPostGrad)).getId(),
                "Post Graduation Course não foi associado à Secretary correta");

        assertEquals(1, savedSecICGrad.getCourses().size(),
                "Course não foi associado à Graduation Secretary corretamente");
        assertEquals(1, savedSecICPostGrad.getCourses().size(),
                "Course não foi associado à Post Graduation Secretary corretamente");
        assertEquals(1, savedSecFDAGrad.getCourses().size(),
                "Course não foi associado à Graduation Secretary corretamente");
        assertEquals(1, savedSecFDAPostGrad.getCourses().size(),
                "Course não foi associado à Post Graduation Secretary corretamente");

        assertEquals(secICgrad.getId(), savedIC.getGraduate().getId(),
                "Graduation Secretary não foi associada ao Department correto");
        assertEquals(secICPostGrad.getId(), savedIC.getPostgraduate().getId(),
                "Post Graduation Secretary não foi associada ao Department correto");
        assertEquals(secFDAGrad.getId(), savedFDA.getGraduate().getId(),
                "Graduation Secretary não foi associada ao Department correto");
        assertEquals(secFDAPostGrad.getId(), savedFDA.getPostgraduate().getId(),
                "Post Graduation Secretary não foi associada ao Department correto");

        assertEquals(0, discipGrad1.getStudents().size(),
             "Graduation Subject possui Students matriculados");
       assertNull(discipGrad1.enroll(stdntNewGrad, IC, IC, secICgrad, secICgrad));
       assertNull(discipGrad1.enroll(stdntOldGrad, IC, IC, secICgrad, secICgrad));
        assertNotNull(discipGrad1.enroll(stdntNewPostGrad, IC, IC, secICPostGrad, secICgrad));
        assertNotNull(discipGrad1.enroll(stdntOldPostGrad, IC, IC, secICPostGrad, secICgrad));
        assertEquals(2, discipGrad1.getStudents().size(),
               "Graduation Subject possui número de Students matriculados diferente do esperado");
      assertTrue(discipGrad1.getStudents().contains(stdntNewGrad),
                "Newbie Graduation student não foi matriculado em " + discipGrad1.getName());
       assertTrue(discipGrad1.getStudents().contains(stdntOldGrad),
                "Veteran Graduation student não foi matriculado em " + discipGrad1.getName());
        assertFalse(discipGrad1.getStudents().contains(stdntNewPostGrad),
                "Newbie Post Graduation student foi matriculado em " + discipGrad1.getName());
        assertFalse(discipGrad1.getStudents().contains(stdntOldPostGrad),
                "Veteran Post Graduation student foi matriculado em " + discipGrad1.getName());

        assertNotNull(discipGrad3.enroll(stdntNewGrad, IC, FDA, secICgrad, secFDAGrad),
                "Newbie Graduation student foi matriculado numa Subject de um Department diferente do student");
        assertNotNull(discipGrad2.enroll(stdntNewPostGrad, IC, IC, secICPostGrad, secICgrad),
                "Newbie Post Graduation student foi matriculado numa Graduation Subject");
        assertNull(discipPostGrad1.enroll(stdntNewPostGrad, IC, IC, secICPostGrad, secICPostGrad),
                "Newbie Post Graduation student não foi matriculado numa Post Graduation Subject");
        assertNotNull(discipPostGrad1.enroll(stdntNewGrad, IC, IC, secICgrad, secICPostGrad),
                "Newbie Graduation student foi matriculado numa Post Graduation Subject ");
        assertNull(discipPostGrad1.enroll(stdntOldGrad, IC, IC, secICgrad, secICPostGrad),
                "Veteran Graduation student não foi matriculado numa Subject de Post Graduation");
        assertNotNull(discipGrad2.enroll(stdntNewGrad, IC, IC, secICgrad, secICgrad),
                "Newbie Graduation student foi matriculado numa Graduation Subject que ele não possui créditos suficientes");
        assertNotNull(discipGrad2.enroll(stdntOldGrad, IC, IC, secICgrad, secICgrad),
                "Veteran Graduation student foi matriculado numa Graduation Subject que ele não atende aos pré-requisitos (Required Subject)");
        assertNotNull(discipGrad1.enroll(stdntOldGrad, IC, IC, secICgrad, secICgrad),
                "Veteran Graduation student foi matriculado numa Graduation Subject em que ele já está matriculado");
        assertTrue(stdntOldGrad.completeSubject(discipGrad1),
                "Veteran Graduation student falhou ao concluir uma Graduation Subject sem requisitos");
        assertNotNull(discipGrad1.enroll(stdntOldGrad, IC, IC, secICgrad, secICgrad),
                "Veteran Graduation student foi matriculado numa Graduation Subject que ele já concluiu");
        assertNull(discipGrad2.enroll(stdntOldGrad, IC, IC, secICgrad, secICgrad),
                "Veteran Graduation student não foi matriculado numa Graduation Subject que ele atende todos os requisitos");

        assertEquals(1, dbTesting.inTransaction(() -> subjectDAO.getAllByStudent(stdntNewGrad)).size(),
                "Quantidade de Subjects em que o Newbie Graduation student se encontra matriculado está diferente do esperado");
        assertEquals(2, dbTesting.inTransaction(() -> subjectDAO.getAllByStudent(stdntOldGrad)).size(),
                "Quantidade de Subjects em que o Veteran Graduation student se encontra matriculado está diferente do esperado");
        assertEquals(1, dbTesting.inTransaction(() -> subjectDAO.getAllByStudent(stdntNewPostGrad)).size(),
                "Quantidade de Subjects em que o Newbie Post Graduation student se encontra matriculado está diferente do esperado");
        assertEquals(0, dbTesting.inTransaction(() -> subjectDAO.getAllByStudent(stdntOldPostGrad)).size(),
                "Quantidade de Subjects em que o Veteran Post Graduation student se encontra matriculado está diferente do esperado");

        savedStdntNewGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewGrad));
        assertEquals(0, (int) savedStdntNewGrad.getCredits(),
                "Newbie Graduation student Credits diferente do esperado");
        assertEquals(0, savedStdntNewGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Newbie Graduation student está diferente do esperado");
        assertTrue(stdntNewGrad.completeSubject(discipGrad1),
                "Newbie Graduation student falhou ao concluir uma Subject sem requisitos");
        assertFalse(stdntNewGrad.completeSubject(discipGrad2),
                "Newbie Graduation student concluiu uma Graduation Subject que não estava matriculado");
        assertFalse(stdntNewGrad.completeSubject(discipGrad3),
                "Newbie Graduation student concluiu uma Graduation Subject de outro Department");
        assertFalse(stdntNewGrad.completeSubject(discipPostGrad1),
                "Newbie Graduation student concluiu uma Post Graduation Subject");
        assertEquals(discipGrad1.getCredits(), stdntNewGrad.getCredits(),
                "Credits obtido pelo Newbiew Graduation student está diferente do esperado");
        assertEquals(1, stdntNewGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Newbiew Graduation student está em diferente do esperado");

        savedStdntOldGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldGrad));
        assertEquals(discipGrad1.getCredits() + 240, (int) savedStdntOldGrad.getCredits(),
                "Veteran Graduation student Credits diferente do esperado");
        assertEquals(1, savedStdntNewGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Veteran Graduation student está diferente do esperado");
        assertFalse(stdntOldGrad.completeSubject(discipGrad1),
                "Veteran Graduation student concluiu uma Subject que ele já havia completado antes");
        assertTrue(stdntOldGrad.completeSubject(discipGrad2),
                "Veteran Graduation student falhou ao concluir uma Graduation Subject, com requisitos, que ele está matriculado");
        assertFalse(stdntOldGrad.completeSubject(discipGrad3),
                "Veteran Graduation student concluiu uma Graduation Subject de outro Department");
        assertTrue(stdntOldGrad.completeSubject(discipPostGrad1),
                "Veteran Graduation student falhou ao concluir uma Post Graduation Subject que ele está matriculado");
        assertEquals(discipGrad1.getCredits() + discipGrad2.getCredits() + discipPostGrad1.getCredits() + 240, (int) stdntOldGrad.getCredits(),
                "Credits obtido pelo Veteran Graduation student está diferente do esperado");
        assertEquals(3, stdntOldGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Veteran Graduation student está em diferente do esperado");

        savedStdntNewPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntNewPostGrad));
        assertEquals(0, (int) savedStdntNewPostGrad.getCredits(),
                "Newbie Post Graduation student Credits diferente do esperado");
        assertEquals(0, savedStdntNewPostGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Newbie Post Graduation student está diferente do esperado");
        assertFalse(stdntNewPostGrad.completeSubject(discipGrad1),
                "Newbie Post Graduation student concluiu uma Graduation Subject sem requisitos");
        assertFalse(stdntNewPostGrad.completeSubject(discipGrad2),
                "Newbie Post Graduation student concluiu uma Graduation Subject com requisitos");
        assertFalse(stdntNewPostGrad.completeSubject(discipGrad3),
                "Newbie Post Graduation student concluiu uma Graduation Subject de outro Department");
        assertTrue(stdntNewPostGrad.completeSubject(discipPostGrad1),
                "Newbie Post Graduation student falhou ao concluir uma Post Graduation Subject em que está matriculado");
        assertEquals(discipPostGrad1.getCredits(), stdntNewPostGrad.getCredits(),
                "Credits obtido pelo Newbiew Post Graduation student está diferente do esperado");
        assertEquals(1, stdntNewPostGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Newbiew Post Graduation student está em diferente do esperado");

        savedStdntOldPostGrad = dbTesting.inTransaction(() -> studentDAO.persist(stdntOldPostGrad));
        assertEquals(300, (int) savedStdntOldPostGrad.getCredits(),
                "Veteran Post Graduation student Credits diferente do esperado");
        assertEquals(0, savedStdntOldPostGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Veteran Post Graduation student está diferente do esperado");
        assertFalse(stdntOldPostGrad.completeSubject(discipGrad1),
                "Veteran Post Graduation student concluiu uma Graduation Subject sem requisitos");
        assertFalse(stdntOldPostGrad.completeSubject(discipGrad2),
                "VEteran Post Graduation student concluiu uma Graduation Subject com requisitos");
        assertFalse(stdntOldPostGrad.completeSubject(discipGrad3),
                "VEteran Post Graduation student concluiu uma Graduation Subject de outro Department");
        assertFalse(stdntOldPostGrad.completeSubject(discipPostGrad1),
                "Veteran Post Graduation student concluiu uma Post Graduation Subject em que não está matriculado");
        assertEquals(300, (int) stdntOldPostGrad.getCredits(),
                "Credits obtido pelo Veteran Post Graduation student está diferente do esperado");
        assertEquals(0, stdntOldPostGrad.getCompletedSubjects().size(),
                "Quantidade de Subjects concluídas pelo Veteran Post Graduation student está em diferente do esperado");

        assertTrue(compEngineeringGrad.deleteSubject(discipGrad1),
                "Falhou ao remover uma Graduation Subject que pertence ao Course");
        assertFalse(compEngineeringGrad.deleteSubject(discipPostGrad1),
                "Removeu uma Post Graduation Subject de um Graduation Course");
        assertFalse(lawGrad.deleteSubject(discipGrad2),
                "Removeu uma Subject que não pertence ao Department do Course");
    }
}
