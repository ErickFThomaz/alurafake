package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.web.dto.NewTaskMultipleChoiceDTO;
import br.com.alura.AluraFake.task.web.dto.NewTaskOpenTextDTO;
import br.com.alura.AluraFake.task.web.dto.NewTaskOptionDTO;
import br.com.alura.AluraFake.task.web.dto.NewTaskSingleChoiceDTO;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskCommandTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    CourseRepository courseRepository;

    @InjectMocks
    TaskCommand taskCommand;

    private Course buildingCourse;

    @BeforeEach
    void setUp() {
        User instructor = new User("Instrutor", "inst@alura.com", Role.INSTRUCTOR);
        buildingCourse = new Course("Java", "Desc", instructor);
        buildingCourse.setStatus(Status.BUILDING);
    }

    @Test
    void createOpenText__should_save_task_and_shift_orders_when_needed() {
        NewTaskOpenTextDTO dto = new NewTaskOpenTextDTO(1L, "Pergunta válida?", 2);
        doReturn(Optional.of(buildingCourse)).when(courseRepository).findById(1L);
        doReturn(true).when(taskRepository).existsByOrderGreaterThanEqual(2);

        taskCommand.createOpenText(dto);

        verify(taskRepository).existsByOrderGreaterThanEqual(2);
        verify(taskRepository).shiftOrders(2);
        verify(taskRepository).save(argThat(task ->
                task.getCourse() == buildingCourse &&
                        task.getType() == Type.OPEN_TEXT &&
                        task.getOrder() == 2 &&
                        task.getStatement().equals("Pergunta válida?")
        ));
    }

    @Test
    void createOpenText__should_throw_not_found_when_course_does_not_exist() {
        doReturn(Optional.empty()).when(courseRepository).findById(999L);
        NewTaskOpenTextDTO dto = new NewTaskOpenTextDTO(999L, "Pergunta válida?", 1);

        assertThatThrownBy(() -> taskCommand.createOpenText(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("O curso não existe");
    }

    @Test
    void createOpenText__should_throw_conflict_when_statement_is_duplicated() {
        NewTaskOpenTextDTO dto = new NewTaskOpenTextDTO(1L, "Pergunta única", 1);
        doReturn(Optional.of(buildingCourse)).when(courseRepository).findById(1L);
        doReturn(Optional.of(mock(Task.class))).when(taskRepository)
                .findByCourseAndStatement(buildingCourse, "Pergunta única");

        assertThatThrownBy(() -> taskCommand.createOpenText(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe uma tarefa com a mesma pergunta");
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createOpenText__should_throw_bad_request_when_course_is_published() {
        buildingCourse.setStatus(Status.PUBLISHED);
        NewTaskOpenTextDTO dto = new NewTaskOpenTextDTO(1L, "Pergunta válida?", 1);
        doReturn(Optional.of(buildingCourse)).when(courseRepository).findById(1L);

        assertThatThrownBy(() -> taskCommand.createOpenText(dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Adicionar novas tarefas com o curso publicado não é possível");
    }

    @Test
    void createSingleChoice__should_save_task_with_options() {
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", false)
        );
        NewTaskSingleChoiceDTO dto = new NewTaskSingleChoiceDTO(1L, "Qual a correta?", 1, options);

        doReturn(Optional.of(buildingCourse)).when(courseRepository).findById(1L);
        taskCommand.createSingleChoice(dto);

        verify(taskRepository).save(argThat(task ->
                task.getType() == Type.SINGLE_CHOICE && task.getOptions().size() == 2));
    }

    @Test
    void createMultipleChoice__should_save_task_with_options_and_shift_if_needed() {
        when(taskRepository.existsByOrderGreaterThanEqual(3)).thenReturn(true);
        List<NewTaskOptionDTO> options = List.of(
                new NewTaskOptionDTO("Opção 1", true),
                new NewTaskOptionDTO("Opção 2", true)
        );
        NewTaskMultipleChoiceDTO dto = new NewTaskMultipleChoiceDTO(1L, "Selecione as corretas", 3, options);

        doReturn(Optional.of(buildingCourse)).when(courseRepository).findById(1L);
        taskCommand.createMultipleChoice(dto);

        verify(taskRepository).shiftOrders(3);
        verify(taskRepository).save(argThat(task -> task.getType() == Type.MULTIPLE_CHOICE));
    }
}
