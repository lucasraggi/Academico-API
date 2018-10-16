package br.ufal.ic.academico.api.secretary;

import br.ufal.ic.academico.api.subject.Subject;
import br.ufal.ic.academico.api.subject.SubjectDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
public class SecretaryDTO {

    public String type;
    public List<SubjectDTO> subjects = new LinkedList<>();

    public SecretaryDTO(Secretary entity) {
        this.type = entity.type;
        if (entity.courses != null) {
            ArrayList<SubjectDTO> dtoList = new ArrayList<>();
            entity.courses.forEach(c -> dtoList.addAll(c.getSubjects().stream().map(SubjectDTO::new).collect(Collectors.toList())));
            this.subjects = dtoList;
        }
    }


    @Getter
    @RequiredArgsConstructor
    @AllArgsConstructor
    @ToString
    private class SubjectDTO {
        Long id;
        String code, name;
        Integer credits, requiredCredits;
        List<String> requiredSubjects;

        SubjectDTO(Subject entity) {
            this.id = entity.getId();
            this.code = entity.getCode();
            this.code = entity.getCode();
            this.name = entity.getName();
            this.credits = entity.getCredits();
            this.requiredCredits = entity.getRequiredCredits();
            this.requiredSubjects = entity.getRequiredSubjects();
        }
    }
}
