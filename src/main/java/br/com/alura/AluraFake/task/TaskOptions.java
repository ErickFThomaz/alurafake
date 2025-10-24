package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TaskOptions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private String optionText;

    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    @Deprecated
    public TaskOptions() {
    }

    public TaskOptions(NewTaskOptionDTO optionDTO, Task task) {
        this.optionText = optionDTO.getOption();
        this.correct = optionDTO.isCorrect();
        this.task = task;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }
}
