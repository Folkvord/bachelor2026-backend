package no.bachelor26.Tasks.Hints;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Tasks.Hints.DTO.HintDTO;

public interface HintRepository extends JpaRepository<Hint, HintId> {
    
    @Query("""
        SELECT new no.bachelor26.Tasks.Hints.DTO.HintDTO(
            h.hintMessage, h.id.index, h.cost
        )
        FROM Hint h
        WHERE h.id.taskID = :taskId
        ORDER BY h.id.index ASC
    """)
    List<HintDTO> findHintsByTaskId(Long taskId);

    boolean existsByIdTaskIDAndIdIndex(Long taskId, Short hintIndex);

}
