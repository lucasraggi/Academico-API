package br.ufal.ic.academico.api.student;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@ToString
public class StudentDTO {
    public Long id;
    public String firstName;
    public String lastName;
    public Integer credits;
    public String course;
    public List<String> completedSubjects = new ArrayList<>();

    public StudentDTO(Student entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.credits = entity.getCredits();

        if (entity.course != null) this.course = entity.course.getName();

        this.completedSubjects = entity.completedSubjects;
    }
}
