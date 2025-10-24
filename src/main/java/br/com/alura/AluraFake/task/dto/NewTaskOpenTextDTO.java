package br.com.alura.AluraFake.task.dto;

import br.com.alura.AluraFake.course.Status;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public class NewTaskOpenTextDTO {

    private Long courseId;

    @Length(min = 4, max = 255)
    private String statement;

    @Min(1)
    private Integer order;

    public Long getCourseId() {
        return courseId;
    }

    public Integer getOrder() {
        return order;
    }

    public String getStatement() {
        return statement;
    }
}
