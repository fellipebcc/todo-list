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

<<<<<<< HEAD
    @JsonProperty("description")
=======
    private Long id;

>>>>>>> 8a145508ae9bb84231ccde42183ffa2aff2410b9
    private String description;

    @JsonProperty("isCompleted")
    private Boolean isCompleted;

    @JsonProperty("deadline")
    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    private LocalDate deadline;

<<<<<<< HEAD
    @Override
    public String toString() {
        String status = isCompleted ? "Completa" : "Incompleta";
        return String.format("Descrição: %s Deadline: %s Status: %s", description, deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), status);
    }


=======
    public Chore(String description, Boolean isCompleted, LocalDate deadline) {
        this.description = description;
        this.isCompleted = isCompleted;
        this.deadline = deadline;
    }
>>>>>>> 8a145508ae9bb84231ccde42183ffa2aff2410b9
}
