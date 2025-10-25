package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.task.dto.NewTaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.dto.NewTaskSingleChoiceDTO;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        validateCourseStatus(course);
        validateStatementUniqueness(course, dto.getStatement());

        if (repository.existsByOrderGreaterThanEqual(dto.getOrder())) {
            repository.shiftOrders(dto.getOrder());
        }

        Task task = new Task(course, Type.OPEN_TEXT, dto.getOrder(), dto.getStatement());

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

        Task task = new Task(course, Type.SINGLE_CHOICE, dto.getOrder(), dto.getStatement(), dto.getOptions());

        repository.save(task);
    }

    public void createMultipleChoice(NewTaskMultipleChoiceDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("O curso não existe"));

        validateCourseStatus(course);
        validateStatementUniqueness(course, dto.getStatement());

        if (repository.existsByOrderGreaterThanEqual(dto.getOrder())) {
            repository.shiftOrders(dto.getOrder());
        }

        Task task = new Task(course, Type.MULTIPLE_CHOICE, dto.getOrder(), dto.getStatement(), dto.getOptions());

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
