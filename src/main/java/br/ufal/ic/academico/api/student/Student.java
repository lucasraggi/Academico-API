package br.ufal.ic.academico.api.student;

import br.ufal.ic.academico.api.course.Course;
import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.Subject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import br.ufal.ic.academico.api.Person;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@RequiredArgsConstructor
public class Student extends Person {
    @Setter
    Integer credits;

    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @Nullable
    Course course;

    @ElementCollection
    List<String> completedSubjects;

    public Student(String firstName, String lastName) {
        super(firstName, lastName);
        this.credits = 0;
        this.completedSubjects = new ArrayList<>();
    }

    public Student(StudentDTO entity) {
        this(entity.firstName, entity.lastName);
    }

    public void update(StudentDTO entity) {
        super.update(entity.firstName, entity.lastName);
    }

    public boolean completeSubject(Subject subject) {

        if (subject.removeStudent(this)) {
            this.credits += subject.getCredits();
            return this.completedSubjects.add(subject.getCode());
        }

        return false;
    }
}
