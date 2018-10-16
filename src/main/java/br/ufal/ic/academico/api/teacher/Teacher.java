package br.ufal.ic.academico.api.teacher;

import br.ufal.ic.academico.api.Person;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;

@Entity
@Getter
@RequiredArgsConstructor
public class Teacher extends Person {
    public Teacher(TeacherDTO entity) {
        this(entity.firstName, entity.lastName);
    }

    public Teacher(String firstName, String lastName) {
        super(firstName, lastName);
    }

    public void update(TeacherDTO entity) {
        super.update(entity.firstName, entity.lastName);
    }
}
