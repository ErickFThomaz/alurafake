package br.com.alura.AluraFake.task.web.dto;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public class NewTaskOpenTextDTO {

    @Min(1)
    private Long courseId;

    @Length(min = 4, max = 255)
    private String statement;

    @Min(1)
    private Integer order;

    public NewTaskOpenTextDTO() {
    }

    public NewTaskOpenTextDTO(Long courseId, String statement, Integer order) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
    }

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
