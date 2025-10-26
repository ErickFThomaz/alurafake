package br.com.alura.AluraFake.course.web.dto;

import java.util.List;

public class InstructorCoursesListDTO {

    private final List<CourseListItemDTO> courses;
    private final long totalPublishedCourses;

    public InstructorCoursesListDTO(List<CourseListItemDTO> courses, long totalPublishedCourses) {
        this.courses = courses;
        this.totalPublishedCourses = totalPublishedCourses;
    }

    public List<CourseListItemDTO> getCourses() {
        return courses;
    }

    public long getTotalPublishedCourses() {
        return totalPublishedCourses;
    }
}
