package br.ufal.ic.academico.api.subject;

import br.ufal.ic.academico.api.student.Student;
import br.ufal.ic.academico.api.student.StudentDTO;
import br.ufal.ic.academico.api.teacher.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class SubjectDTO {

    String name;
    String code;
    Long id;
    Integer credits, requiredCredits;
    List<String> requiredSubjects;
    String degreeLevel;
    String teacher;
    List<StudentDTO> students;

    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.name = subject.name;
        this.code = subject.code;
        this.credits = subject.credits;
        this.requiredCredits = subject.requiredCredits;
        this.requiredSubjects = subject.requiredSubjects;
        if (subject.teacher != null){
            if (subject.teacher.getLastName() == null) this.teacher = subject.teacher.getFirstName();
            else this.teacher = subject.teacher.getFirstName() + " " + subject.teacher.getLastName();
        }
        ArrayList<StudentDTO> dtoList = new ArrayList<>();
        if (subject.students != null) {
            subject.students.forEach(s -> dtoList.add(new StudentDTO(s)));
        }
        this.students = dtoList;
    }

    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @ToString
    private class StudentDTO {
        public Long id;
        public String name;

        StudentDTO(Student entity) {
            this.id = entity.getId();

            if (entity.getLastName() == null) this.name = entity.getFirstName();
            else this.name = entity.getFirstName() + " " + entity.getLastName();
        }
    }
}
