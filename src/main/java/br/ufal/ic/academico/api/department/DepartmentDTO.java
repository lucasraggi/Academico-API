package br.ufal.ic.academico.api.department;

import br.ufal.ic.academico.api.secretary.Secretary;
import br.ufal.ic.academico.api.secretary.SecretaryDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class DepartmentDTO {
    String name;
    List<SecretaryDTO> secretaries = new ArrayList<>();


    public DepartmentDTO(Department department) {
        this.name = department.name;
        if (department.graduate != null) {
            this.secretaries.add(new SecretaryDTO(department.graduate));
        }
        if (department.postgraduate != null) {
            this.secretaries.add(new SecretaryDTO(department.postgraduate));
        }
    }

}
