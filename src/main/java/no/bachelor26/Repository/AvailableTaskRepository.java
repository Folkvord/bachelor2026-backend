package no.bachelor26.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import no.bachelor26.Entity.AvailableTask;
import no.bachelor26.Entity.Task;

public interface AvailableTaskRepository extends JpaRepository<AvailableTask, Long>{

    public List<Task> findTaskByUserId(UUID userID);

    public boolean existsByUserIdAndTaskId(UUID userID, Long taskID);

}
