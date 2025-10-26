package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.task.web.dto.NewTaskOptionDTO;
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

    public Task(Course course, Type type, int order, String statement){
        this.course = course;
        this.type = type;
        this.order = order;
        this.statement = statement;
    }


    public Task(Course course, Type type, int order, String statement, List<NewTaskOptionDTO> options){
        this.course = course;
        this.type = type;
        this.order = order;
        this.statement = statement;
        this.options = options.stream().map(optionDTO -> new TaskOptions(optionDTO, this)).toList();
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
