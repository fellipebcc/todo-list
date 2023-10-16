package br.edu.unifalmg.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("description")
    private String description;

    @JsonProperty("isCompleted")
    private Boolean isCompleted;

    @JsonProperty("deadline")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private LocalDate deadline;

    @Override
    public String toString() {
        String status = isCompleted ? "Completa" : "Incompleta";
        return String.format("Descrição: %s Deadline: %s Status: %s", description, deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), status);
    }


}
