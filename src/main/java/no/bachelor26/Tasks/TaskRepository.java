package no.bachelor26.Tasks;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Tasks.DTO.RawTaskComponents;

public interface TaskRepository extends JpaRepository<Task, Integer>{

    @Query("""
        SELECT new no.bachelor26.Tasks.DTO.RawTaskComponents(
            t.taskData, t.staticFlag, t.unlocksTaskID
        )
        FROM Task t
        WHERE t.id = :id
    """)
    Optional<RawTaskComponents> findTaskContentById(Integer id);

    @Query("SELECT t.id FROM Task t")
    List<Integer> findAllIDs();

    boolean existsById(Integer id);

}
