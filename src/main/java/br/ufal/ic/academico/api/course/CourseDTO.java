package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class CourseDTO {

    String name;
    List<SubjectDTO> subjects;

    public CourseDTO(Course course) {
        this.name = course.name;

        LinkedList<SubjectDTO> subjects = new LinkedList<>();
        course.subjects.forEach(c -> subjects.addLast(new SubjectDTO(c)));
        this.subjects = subjects;
    }
}
