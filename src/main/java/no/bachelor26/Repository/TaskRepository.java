package no.bachelor26.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Entity.Task;
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
        SELECT t.task as task
        FROM Task t
        WHERE t.id = :id        
    """)
    Optional<String> findTaskContentById(Long id);


}
