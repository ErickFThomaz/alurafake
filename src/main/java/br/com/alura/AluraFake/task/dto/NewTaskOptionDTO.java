package br.com.alura.AluraFake.task.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public class NewTaskOptionDTO {

    @Length(min = 4, max = 80)
    private String option;

    @JsonProperty("isCorrect")
    private boolean isCorrect;

    public String getOption() {
        return option;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
