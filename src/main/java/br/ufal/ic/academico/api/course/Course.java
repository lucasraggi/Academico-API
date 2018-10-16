package br.ufal.ic.academico.api.course;

import br.ufal.ic.academico.api.subject.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@RequiredArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Setter
    String name;

    @ManyToMany(cascade = CascadeType.ALL)
    List<Subject> subjects;

    public Course(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
    }

    public Course(CourseDTO course) {
        if (course.name != null) {
            this.name = course.name;
        }

    }

    public void update(CourseDTO course) {
        if (course.name != null) {
            this.name = course.name;
        }
    }

    public void addSubject(Subject subject) {
        this.subjects.add(subject);
    }

    public boolean deleteSubject(Subject subject) {
        return this.subjects.remove(subject);
    }
}
