package no.bachelor26.Tasks.TaskAccess;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskAccessRepository extends JpaRepository<TaskAccess, TaskAccessId>{
    
    @Query("""
        SELECT t.id.taskID 
        FROM TaskAccess t
        WHERE t.id.userID = :userID
    """)
    public List<Long> findAllAccessableTasks(UUID userID);

    public boolean existsByIdUserIDAndIdTaskID(UUID userID, Long taskID);

}
