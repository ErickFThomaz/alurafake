package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    private Course course;

    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(name = "order_id")
    private Integer order;

    private String statement;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TaskOptions> options;

    @Deprecated
    public Task(){
    }

    public Task(Course course, Type type, NewTaskOpenTextDTO dto){
        this.course = course;
        this.type = type;
        this.order = dto.getOrder();
        this.statement = dto.getStatement();
    }

    public Task(Course course, Type type, NewTaskSingleChoiceDTO dto){
        this.course = course;
        this.type = type;
        this.order = dto.getOrder();
        this.statement = dto.getStatement();
        this.options = dto.getOptions().stream().map(optionDTO -> new TaskOptions(optionDTO, this)).toList();
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Course getCourse() {
        return course;
    }

    public Type getType() {
        return type;
    }

    public Integer getOrder() {
        return order;
    }

    public String getStatement() {
        return statement;
    }

    public List<TaskOptions> getOptions() {
        return options;
    }
}
