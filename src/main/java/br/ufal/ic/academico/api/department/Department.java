package br.ufal.ic.academico.api.department;

import br.ufal.ic.academico.api.secretary.Secretary;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.*;


@Entity
@Getter
@RequiredArgsConstructor
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Setter
    String name;

    @OneToOne(cascade = CascadeType.ALL)
    @Setter
    Secretary graduate, postgraduate;

    public Department(String name) {
        this.name = name;
    }

    public Department(DepartmentDTO department) {
        this(department.name);
    }

    public void update(DepartmentDTO entity) {
        if (entity.name != null) {
            this.name = entity.name;
        }
    }
}
