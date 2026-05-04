package no.bachelor26.Tasks.TaskAccess;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskAccessRepository extends JpaRepository<TaskAccess, TaskAccessId>{
    
    @Query("""
        SELECT t.task.id 
        FROM TaskAccess t
        WHERE t.user.id = :userID
    """)
    public List<Long> findAllAccessableTasks(UUID userID);

    public boolean existsByIdUserIDAndTaskId(UUID userID, Long taskID);

}
