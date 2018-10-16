package br.ufal.ic.academico.api.teacher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@RequiredArgsConstructor
@ToString
public class TeacherDTO {
    public Long id;
    public String firstName, lastName;

    public TeacherDTO(Teacher entity) {
        this.id = entity.getId();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
    }
}
