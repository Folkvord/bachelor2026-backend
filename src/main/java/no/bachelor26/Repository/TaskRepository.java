package no.bachelor26.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Entity.Task;
import no.bachelor26.Projection.TaskContent;
import no.bachelor26.Projection.TaskInfo;

public interface TaskRepository extends JpaRepository<Task, Long>{

    Optional<Task> findByName(String name);

    @Query("""
        SELECT t.id AS id, t.name AS name, t.description AS description
        FROM Task t
        WHERE t.id = :id
    """)
    Optional<TaskInfo> findInfoById(Long id);

    @Query("""
        SELECT t.task as task, t.staticFlag as staticFlag
        FROM Task t
        WHERE t.id = :id
    """)
    Optional<TaskContent> findTaskContentById(Long id);


}
