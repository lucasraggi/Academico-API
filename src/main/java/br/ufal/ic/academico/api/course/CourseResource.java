package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDAO;
import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDAO;
import br.ufal.ic.academico.api.subject.SubjectDTO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;

@Path("course")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class CourseResource {
    private final SecretaryDAO secretaryDAO;
    private final CourseDAO courseDAO;
    private final SubjectDAO subjectDAO;

    @GET
    @UnitOfWork
    public Response getAll() {
        log.info("getAll Courses");
        ArrayList<Course> courses = courseDAO.getAll();
        return(Response.ok(courses).build());
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getById(@PathParam("id") Long id) {

        log.info("getById Course: id={}", id);
        Course c = courseDAO.get(id);
        if (c == null) {
            return Response.status(404).entity("Course not found").build();
        }
        return(Response.ok(c).build());
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, CourseDTO course) {
        log.info("update course {} to {}", id, course);

        Course c = courseDAO.get(id);
        if (c == null) {
            return Response.status(404).entity("Course not found.").build();
        }
        c.update(course);
        return Response.ok(new CourseDTO(courseDAO.persist(c))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE course {}", id);

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();


        Secretary s = courseDAO.getSecretary(c);
        s.deleteCourse(c);

        secretaryDAO.persist(s);
        courseDAO.delete(c);
        return Response.noContent().build();
    }


    @GET
    @Path("/{id}/subjects")
    @UnitOfWork
    public Response getAllSubjects(@PathParam("id") Long id) {
        log.info("get all subjects from course {}", id);

        Course c = courseDAO.get(id);
        if (c == null) {
            return Response.status(404).entity("Course not found.").build();
        }

        assert c.getSubjects() != null;
        return Response.ok(c.getSubjects().stream().map(SubjectDTO::new).toArray()).build();
    }

    @POST
    @Path("/{id}/subject")
    @UnitOfWork
    @Consumes("application/json")
    public Response create(@PathParam("id") Long id, SubjectDTO entity) {
        log.info("create subject in course {}", id);

        if (entity.getCode() == null) return Response.status(400).entity("Precisa fornecer o código da disciplina.").build();

        Course c = courseDAO.get(id);
        if (c == null) return Response.status(404).entity("Este curso não existe.").build();

        Subject d = new Subject(entity);
        subjectDAO.persist(d);

        c.addSubject(d);
        courseDAO.persist(c);

        return Response.ok(new SubjectDTO(d)).build();
    }
}
