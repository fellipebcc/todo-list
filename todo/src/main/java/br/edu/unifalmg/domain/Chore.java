package br.edu.unifalmg.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
        String status = isCompleted ? "Completa" : "Incompleta";
        return String.format("Descrição: %s Deadline: %s Status: %s", description, deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), status);
    }


}
