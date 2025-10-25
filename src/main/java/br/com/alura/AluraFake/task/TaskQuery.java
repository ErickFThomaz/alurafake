package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskQuery {

    private final TaskRepository repository;

    @Autowired
    public TaskQuery(TaskRepository repository) {
        this.repository = repository;
    }

    public List<Task> findAllByCourse(Course course) {
        return repository.findAllByCourse(course);
    }
}
