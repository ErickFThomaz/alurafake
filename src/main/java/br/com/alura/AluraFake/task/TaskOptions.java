package br.com.alura.AluraFake.task;

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
}
