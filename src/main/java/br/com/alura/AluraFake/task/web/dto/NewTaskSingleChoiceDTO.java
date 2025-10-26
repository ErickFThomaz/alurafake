package br.com.alura.AluraFake.task.web.dto;

import br.com.alura.AluraFake.util.validator.ValidSingleChoiceTask;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@ValidSingleChoiceTask
public class NewTaskSingleChoiceDTO {

    @Min(1)
    private Long courseId;

    @Length(min = 4, max = 255)
    private String statement;

    @Min(1)
    private Integer order;

    @Size(min = 2, max = 5)
    private List<NewTaskOptionDTO> options;

    public NewTaskSingleChoiceDTO() {
    }

    public NewTaskSingleChoiceDTO(Long courseId, String statement, Integer order, List<NewTaskOptionDTO> options) {
        this.courseId = courseId;
        this.statement = statement;
        this.order = order;
        this.options = options;
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

    public List<NewTaskOptionDTO> getOptions() {
        return options;
    }
}
