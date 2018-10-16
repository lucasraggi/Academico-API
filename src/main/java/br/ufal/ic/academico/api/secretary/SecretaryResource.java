package br.ufal.ic.academico.api.secretary;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.course.CourseDAO;
import br.ufal.ic.academico.api.course.CourseDTO;
import br.ufal.ic.academico.api.department.Department;
import br.ufal.ic.academico.api.department.DepartmentDAO;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("secretary")
@Slf4j
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
public class SecretaryResource {

    private final DepartmentDAO departmentDAO;
    private final SecretaryDAO secretaryDAO;
    private final CourseDAO courseDAO;

    @GET
    @UnitOfWork
    public Response getAll() {

        log.info("getAll");
        ArrayList<Secretary> secretaries = secretaryDAO.getAll();
        return(Response.ok(secretaries).build());
    }

    @GET
    @Path("/{id}")
    @UnitOfWork
    public Response getById(@PathParam("id") Long id) {

        log.info("getById: id={}", id);
        Secretary s = secretaryDAO.get(id);
        return(Response.ok(s).build());
    }

    @GET
    @Path("/{id}/courses")
    @UnitOfWork
    public Response getAllCourses(@PathParam("id") Long id) {
        log.info("GETALL courses from secretary {}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();

        return Response.ok(s.getCourses().stream().map(Course::getName).toArray()).build();
    }

    @DELETE
    @Path("/{id}")
    @UnitOfWork
    public Response delete(@PathParam("id") Long id) {
        log.info("DELETE secretary: id={}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) return Response.status(404).entity("Secretaria não existe.").build();
        Department d = secretaryDAO.getDepartment(s);
        if (s.getType().equals("GRADUATION")) d.setGraduate(null);
        else d.setPostgraduate(null);
        departmentDAO.persist(d);
        secretaryDAO.delete(s);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/course")
    @UnitOfWork
    @Consumes("application/json")
    public Response createCourse(@PathParam("id") Long id, CourseDTO entity) {
        log.info("create course on secretary {}", id);

        Secretary s = secretaryDAO.get(id);
        if (s == null) {
            return Response.status(404).entity("Secretary not found.").build();
        }

        Course c = new Course(entity);
        if (s.addCourse(c)) {
            courseDAO.persist(c);
            return Response.ok(new SecretaryDTO(secretaryDAO.persist(s))).build();
        }
        return Response.status(400).build();
    }
}
