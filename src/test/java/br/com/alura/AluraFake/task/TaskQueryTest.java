package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskQueryTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskQuery taskQuery;

    @Test
    void findAllByCourse__should_delegate_to_repository() {
        Course course = mock(Course.class);
        List<Task> tasks = List.of(mock(Task.class), mock(Task.class));
        doReturn(tasks).when(taskRepository).findAllByCourse(course);

        List<Task> result = taskQuery.findAllByCourse(course);

        verify(taskRepository, times(1)).findAllByCourse(course);
        assertThat(result).isEqualTo(tasks);
    }
}
