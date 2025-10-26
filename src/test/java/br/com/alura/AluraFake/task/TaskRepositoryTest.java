package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "classpath:sql/clean.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private Course course;

    @BeforeEach
    void setup() {
        User instructor = new User("Instrutor", "inst@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);
        course = new Course("Java", "Desc", instructor);
        courseRepository.save(course);
    }

    @Test
    void findByCourseAndStatement__should_return_present_when_exists_and_empty_when_not() {
        Task t1 = new Task(course, Type.OPEN_TEXT, 1, "Qual a sua dúvida?");
        taskRepository.save(t1);

        Optional<Task> found = taskRepository.findByCourseAndStatement(course, "Qual a sua dúvida?");
        assertThat(found).isPresent();
        assertThat(found.get().getType()).isEqualTo(Type.OPEN_TEXT);

        Optional<Task> notFound = taskRepository.findByCourseAndStatement(course, "Outra pergunta");
        assertThat(notFound).isEmpty();
    }

    @Test
    void existsByOrderGreaterThanEqual__should_reflect_existing_orders() {
        taskRepository.save(new Task(course, Type.OPEN_TEXT, 1, "P1"));
        taskRepository.save(new Task(course, Type.SINGLE_CHOICE, 3, "P2", List.of()));

        assertThat(taskRepository.existsByOrderGreaterThanEqual(1)).isTrue();
        assertThat(taskRepository.existsByOrderGreaterThanEqual(2)).isTrue();
        assertThat(taskRepository.existsByOrderGreaterThanEqual(3)).isTrue();
        assertThat(taskRepository.existsByOrderGreaterThanEqual(4)).isFalse();
    }

    @Test
    void shiftOrders__should_increment_orders_greater_or_equal_threshold() {
        Task a = taskRepository.save(new Task(course, Type.OPEN_TEXT, 1, "A"));
        Task b = taskRepository.save(new Task(course, Type.SINGLE_CHOICE, 2, "B", List.of()));
        Task c = taskRepository.save(new Task(course, Type.MULTIPLE_CHOICE, 4, "C", List.of()));

        // shift from order 2: orders 2 and 4 should become 3 and 5; order 1 unchanged
        taskRepository.shiftOrders(2);
        // Clear persistence context to avoid stale state after bulk update
        entityManager.flush();
        entityManager.clear();

        List<Task> all = taskRepository.findAllByCourse(course);
        // Ensure we have three tasks and orders adjusted
        assertThat(all).hasSize(3);
        assertThat(all.stream().filter(t -> t.getOrder() == 1).count()).isEqualTo(1L);
        assertThat(all.stream().filter(t -> t.getOrder() == 3).count()).isEqualTo(1L);
        assertThat(all.stream().filter(t -> t.getOrder() == 5).count()).isEqualTo(1L);
    }
}
