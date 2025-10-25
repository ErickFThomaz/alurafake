package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByCourse(Course course);

    Optional<Task> findByCourseAndStatement(Course course, String statement);

    @Modifying
    @Query("UPDATE Task i SET i.order = i.order + 1 WHERE i.order >= :newOrder")
    void shiftOrders(@Param("newOrder") Integer newOrder);

    boolean existsByOrderGreaterThanEqual(Integer order);
}
