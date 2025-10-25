package br.com.alura.AluraFake.course;

import java.util.List;

public class InstructorCoursesResponse {

    private final List<CourseListItemDTO> courses;
    private final long totalPublishedCourses;

    public InstructorCoursesResponse(List<CourseListItemDTO> courses, long totalPublishedCourses) {
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
