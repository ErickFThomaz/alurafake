package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseQueryTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseQuery courseQuery;

    private User instructor;
    private User student;

    @BeforeEach
    void setUp() {
        instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
        student = new User("Aluno", "aluno@alura.com.br", Role.STUDENT);
    }

    @Test
    void findAllByInstructorId__should_throw_when_user_not_found() {
        doReturn(Optional.empty()).when(userRepository).findById(10L);
        assertThatThrownBy(() -> courseQuery.findAllByInstructorId(10L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Instrutor não encontrado");
    }

    @Test
    void findAllByInstructorId__should_throw_when_user_is_not_instructor() {
        doReturn(Optional.of(student)).when(userRepository).findById(2L);
        assertThatThrownBy(() -> courseQuery.findAllByInstructorId(2L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("O usuário não é um instrutor");
    }

    @Test
    void findAllByInstructorId__should_return_courses_and_totals() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(instructor));
        Course java = new Course("Java", "Curso de Java", instructor);
        Course spring = new Course("Spring", "Curso de Spring", instructor);
        spring.setStatus(Status.PUBLISHED);
        doReturn(Arrays.asList(java, spring)).when(courseRepository).findAllByInstructor(instructor);

        InstructorCoursesResponse response = courseQuery.findAllByInstructorId(1L);

        verify(courseRepository).findAllByInstructor(instructor);
        assertThat(response.getCourses()).hasSize(2);
        assertThat(response.getCourses().get(0).getTitle()).isEqualTo("Java");
        assertThat(response.getTotalPublishedCourses()).isEqualTo(1);
    }
}
