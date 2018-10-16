package br.ufal.ic.academico.api.subject;


import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("subject")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class SubjectResource {
    private final CourseDAO courseDAO;
    private final SubjectDAO subjectDAO;

    @GET
    @UnitOfWork
    public Response getAll() {

        log.info("getAll");
        ArrayList<Subject> subjects = subjectDAO.getAll();
        return(Response.ok(subjects).build());
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response get(@PathParam("id") Long id) {
        log.info("GET subject: id={}", id);

        Subject d = subjectDAO.get(id);
        if (d == null) return Response.status(404).entity("Subject not found.").build();

        return Response.ok(new SubjectDTO(d)).build();
    }

    @PUT
    @Path("/{id}")
    @UnitOfWork
    @Consumes("application/json")
    public Response update(@PathParam("id") Long id, SubjectDTO entity) {
        log.info("update subject: id={}", id);

        Subject c = subjectDAO.get(id);
        if (c == null) {
            return Response.status(404).entity("Subject not found.").build();
        }
        c.update(entity);
        return Response.ok(new SubjectDTO(subjectDAO.persist(c))).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE subject {}", id);

        Subject d = subjectDAO.get(id);
        if (d == null) return Response.status(404).entity("Subject not found").build();

        Course c = subjectDAO.getCourse(d);
        c.deleteSubject(d);
        courseDAO.persist(c);
        subjectDAO.delete(d);
        return Response.noContent().build();
    }

}
