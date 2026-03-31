package no.bachelor26.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Entity.Task;
import no.bachelor26.Projection.TaskComponents;

public interface TaskRepository extends JpaRepository<Task, Long>{

    @Query("""
        SELECT t.taskData as task, t.staticFlag as staticFlag
        FROM Task t
        WHERE t.id = :id
    """)
    Optional<TaskComponents> findTaskContentById(Long id);

}
