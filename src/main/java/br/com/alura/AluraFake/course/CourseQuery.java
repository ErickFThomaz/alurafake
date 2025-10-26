package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.course.web.dto.CourseListItemDTO;
import br.com.alura.AluraFake.course.web.dto.InstructorCoursesListDTO;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.exception.BadRequestException;
import br.com.alura.AluraFake.util.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CourseQuery {

    private final CourseRepository repository;

    private final UserRepository userRepository;

    public CourseQuery(CourseRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public InstructorCoursesListDTO findAllByInstructorId(Long instructorId) {
        User user = userRepository.findById(instructorId).orElseThrow(() -> new ResourceNotFoundException("Instrutor não encontrado"));

        if (!user.isInstructor()) {
            throw new BadRequestException("O usuário não é um instrutor");
        }

        List<Course> courses = repository.findAllByInstructor(user);
        List<CourseListItemDTO> courseResponses = courses.stream().map(CourseListItemDTO::new).collect(Collectors.toList());

        long totalPublishedCourses = courses.stream().filter(Course::isPublished).count();

        return new InstructorCoursesListDTO(courseResponses, totalPublishedCourses);
    }
}
