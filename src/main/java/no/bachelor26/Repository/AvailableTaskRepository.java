package no.bachelor26.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Projection.TaskInfo;

public interface AvailableTaskRepository extends JpaRepository<AvailableTask, Long>{

    // Sjekker om en bruker har tilgang til en oppgave
    public boolean existsByUserIdAndTaskId(UUID userID, Long taskID);

    // Finner informasjonen om tilgjengelige oppgaver
    @Query("""
        SELECT a.task.id as id,
        a.task.name as name,
        a.task.description as description
        FROM AvailableTask as a
        WHERE a.user.id = :userID
    """)
    public List<TaskInfo> findInfoByUserId(UUID userID);

}
