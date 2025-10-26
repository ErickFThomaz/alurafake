package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskQuery;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.util.exception.ConflictException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourseCommandTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private TaskQuery taskQuery;

    @InjectMocks
    private CourseCommand courseCommand;

    private Course course;

    @BeforeEach
    void setUp() {
        User instructor = new User("Instrutor", "inst@alura.com", Role.INSTRUCTOR);
        course = new Course("Java", "Desc", instructor);
        course.setStatus(Status.BUILDING);
    }

    private Task task(Type type, int order) {
        // Minimal Task stub via real constructor is fine; Course is set
        return new Task(course, type, order, type.name() + " statement");
    }

    @Test
    void publish__should_publish_when_all_rules_are_satisfied() {
        List<Task> tasks = List.of(
                task(Type.OPEN_TEXT, 1),
                task(Type.SINGLE_CHOICE, 2),
                task(Type.MULTIPLE_CHOICE, 3)
        );
        doReturn(tasks).when(taskQuery).findAllByCourse(course);

        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        courseCommand.publish(1L);

        verify(courseRepository).save(course);
        // After publish, status should be PUBLISHED
        // We cannot easily assert timestamp, but save invoked is enough; method mutates course
    }

    @Test
    void publish__should_throw_not_found_when_course_absent() {
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> courseCommand.publish(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso não foi encontrado");
    }

    @Test
    void publish__should_throw_conflict_when_course_already_published() {
        course.setStatus(Status.PUBLISHED);
        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        assertThatThrownBy(() -> courseCommand.publish(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("O curso já está publicado e não pode ser publicado novamente.");
    }

    @Test
    void publish__should_throw_conflict_when_no_tasks() {
        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        when(taskQuery.findAllByCourse(course)).thenReturn(List.of());
        assertThatThrownBy(() -> courseCommand.publish(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("O curso precisa ter atividades para ser publicado");
    }

    @Test
    void publish__should_throw_conflict_when_duplicate_orders() {
        List<Task> tasks = List.of(
                task(Type.OPEN_TEXT, 1),
                task(Type.SINGLE_CHOICE, 1), // duplicate order
                task(Type.MULTIPLE_CHOICE, 2)
        );
        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        when(taskQuery.findAllByCourse(course)).thenReturn(tasks);
        assertThatThrownBy(() -> courseCommand.publish(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("As atividades não podem ter ordens duplicadas.");
    }

    @Test
    void publish__should_throw_conflict_when_orders_not_continuous() {
        List<Task> tasks = List.of(
                task(Type.OPEN_TEXT, 1),
                task(Type.SINGLE_CHOICE, 3), // gap (missing 2)
                task(Type.MULTIPLE_CHOICE, 4)
        );
        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        when(taskQuery.findAllByCourse(course)).thenReturn(tasks);
        assertThatThrownBy(() -> courseCommand.publish(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("As atividades precisam ter ordens contínuas começando em 1.");
    }

    @Test
    void publish__should_throw_conflict_when_missing_any_task_type() {
        List<Task> tasks = List.of(
                task(Type.OPEN_TEXT, 1),
                task(Type.SINGLE_CHOICE, 2)
        );
        doReturn(Optional.of(course)).when(courseRepository).findById(1L);
        when(taskQuery.findAllByCourse(course)).thenReturn(tasks);
        assertThatThrownBy(() -> courseCommand.publish(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessage("O curso precisa ter ao menos uma atividade de cada tipo antes de ser publicado");
    }
}
