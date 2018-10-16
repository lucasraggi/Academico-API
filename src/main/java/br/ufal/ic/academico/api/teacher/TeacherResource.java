package br.ufal.ic.academico.api.teacher;

import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDAO;
import br.ufal.ic.academico.api.subject.SubjectDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("teacher")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class TeacherResource {

    private final TeacherDAO teacherDAO;
    private final SubjectDAO subjectDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("GETALL teachers");
        return Response.ok(teacherListToDTOList(teacherDAO.getAll())).build();
    }

    @POST
    @UnitOfWork
    @Consumes("application/json")
    public Response create(TeacherDTO entity) {
        log.info("CREATE teacher: {}", entity);

        if (entity.firstName == null) return Response.status(400).entity("Você precisa informar os dados.").build();
        Teacher t = new Teacher(entity);
        return Response.ok(new TeacherDTO(teacherDAO.persist(t))).build();
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET teacher: id={}", id);

        Teacher t = teacherDAO.get(id);
        if (t != null) return Response.ok(new TeacherDTO(t)).build();
        return Response.status(404).entity("Este teacher não está registrado.").build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, TeacherDTO entity) {
        log.info("UPDATE teacher: id={}", id);

        Teacher t = teacherDAO.get(id);
        if (t == null) return Response.status(404).entity("Este teacher não está registrado.").build();
        t.update(entity);
        return Response.ok(new TeacherDTO(teacherDAO.persist(t))).build();
    }

    @POST
    @Path("/{idP}/subject/{idD}")
    @UnitOfWork
    public Response allocate(@PathParam("idP") Long idP, @PathParam("idD") Long idD) {
        log.info("ALLOCATE teacher {} in subject {}", idP, idD);

        Teacher t = teacherDAO.get(idP);
        if (t == null) return Response.status(404).entity("Este teacher não está registrado.").build();

        Subject d = subjectDAO.get(idD);
        if (d == null) return Response.status(404).entity("Esta disciplina não está registrado.").build();

        d.setTeacher(t);
        return Response.ok(new SubjectDTO(subjectDAO.persist(d))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE teacher: id={}", id);

        Teacher t = teacherDAO.get(id);
        if (t == null) return Response.status(404).entity("Este teacher não está registrado.").build();

        subjectDAO.deallocateTeacherFromAllSubjects(t);
        teacherDAO.delete(t);
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    private List<TeacherDTO> teacherListToDTOList(List<Teacher> list) {

        List<TeacherDTO> dtoList = new ArrayList<>();
        list.forEach(s -> dtoList.add(new TeacherDTO(s)));

        return dtoList;
    }

}
