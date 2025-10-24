package br.com.alura.AluraFake.course;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>{

    Optional<Course> findByIdAndStatus(Long id, Status status);
}
