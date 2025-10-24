package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class TaskCommand {

    private final TaskRepository repository;

    private final CourseRepository courseRepository;

    @Autowired
    public TaskCommand(TaskRepository repository, CourseRepository courseRepository) {
        this.repository = repository;
        this.courseRepository = courseRepository;
    }

    public void createOpenText(NewTaskOpenTextDTO dto){
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("O curso não existe"));

        if(!course.isBuildingStatus()) {
            throw new BadRequestException("Adicionar novas tarefas com o curso publicado não é possivel");
        }

        if (repository.findByCourseAndStatement(course, dto.getStatement().trim()).isPresent()) {
            throw new ConflictException("Já existe uma tarefa com a mesma pergunta");
        }

        if (repository.existsByOrderGreaterThanEqual(dto.getOrder())) {
            repository.shiftOrders(dto.getOrder());
        }

        Task task = new Task(course, Type.OPEN_TEXT, dto);

        repository.save(task);
    }

    public void createSingleChoice(NewTaskSingleChoiceDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("O curso não existe"));

        validateCourseStatus(course);
        validateStatementUniqueness(course, dto.getStatement());

        if (repository.existsByOrderGreaterThanEqual(dto.getOrder())) {
            repository.shiftOrders(dto.getOrder());
        }

        Task task = new Task(course, Type.SINGLE_CHOICE, dto);

        repository.save(task);
    }

    private void validateCourseStatus(Course course) {
        if (!course.isBuildingStatus()) {
            throw new BadRequestException("Adicionar novas tarefas com o curso publicado não é possível");
        }
    }

    private void validateStatementUniqueness(Course course, String rawStatement) {
        String statement = rawStatement.trim();
        if (repository.findByCourseAndStatement(course, statement).isPresent()) {
            throw new ConflictException("Já existe uma tarefa com a mesma pergunta");
        }
    }

}
