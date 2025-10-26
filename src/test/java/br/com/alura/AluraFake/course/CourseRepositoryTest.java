package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    private User instructor;

    @BeforeEach
    void setup() {
        instructor = new User("Instrutor", "inst@alura.com", Role.INSTRUCTOR);
        userRepository.save(instructor);
    }

    @Test
    void findAllByInstructor__should_return_courses_for_instructor() {
        Course java = new Course("Java", "Desc", instructor);
        Course spring = new Course("Spring", "Desc", instructor);
        courseRepository.saveAll(List.of(java, spring));

        List<Course> found = courseRepository.findAllByInstructor(instructor);
        assertThat(found).hasSize(2);
        assertThat(found.get(0).getInstructor().getEmail()).isEqualTo(instructor.getEmail());
    }
}
