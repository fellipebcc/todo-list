package br.edu.unifalmg.domain;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chore {

    private String description;

    private Boolean isCompleted;

    private LocalDate deadline;

    @Override
    public String toString() {
        return "Descrição: " + description + " Deadline: " + deadline +
                " Status: " + isCompleted;
    }
}
