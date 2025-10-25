package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskQuery;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class CourseCommand {

    private final TaskQuery taskQuery;

    private final CourseRepository repository;



    @Autowired
    public CourseCommand(CourseRepository repository, TaskQuery taskQuery) {
        this.repository = repository;
        this.taskQuery = taskQuery;
    }

    public void publish(Long id){
        Course course = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não foi encontrado"));

        if(!course.isBuildingStatus()) {
            throw new ConflictException("O curso já está publicado e não pode ser publicado novamente.");
        }

        List<Task> tasks = taskQuery.findAllByCourse(course);

        validateSequentialOrder(tasks);
        validateHasAllTaskTypes(tasks);

        repository.save(course.publish());
    }

    private void validateHasAllTaskTypes(List<Task> tasks) {
        Set<Type> tiposPresentes = tasks.stream()
                .map(Task::getType)
                .collect(Collectors.toSet());

        if (!tiposPresentes.containsAll(EnumSet.allOf(Type.class))) {
            throw new ConflictException("O curso precisa ter ao menos uma atividade de cada tipo antes de ser publicado");
        }
    }

    private void validateSequentialOrder(List<Task> tasks) {
        if (tasks.isEmpty()) {
            throw new ConflictException("O curso precisa ter atividades para ser publicado");
        }

        Set<Integer> uniqueOrders = tasks.stream()
                .map(Task::getOrder)
                .collect(Collectors.toSet());

        int expectedSize = tasks.size();

        if (uniqueOrders.size() != expectedSize) {
            throw new ConflictException("As atividades não podem ter ordens duplicadas.");
        }

        Integer maxOrder = uniqueOrders.stream()
                .max(Comparator.naturalOrder())
                .orElse(0);

        if (maxOrder != expectedSize) {
            throw new ConflictException("As atividades precisam ter ordens contínuas começando em 1.");
        }
    }
}
