package no.bachelor26.Tasks.Hints;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import no.bachelor26.Tasks.Hints.DTO.HintDTO;

public interface HintRepository extends JpaRepository<Hint, Long> {
    
    @Query("""
        SELECT new no.bachelor26.Tasks.Hints.DTO.HintDTO(
            h.hintMessage, h.hintIndex, h.cost
        )
        FROM Hint h
        WHERE h.task.id = :taskId
        ORDER BY h.hintIndex ASC
    """)
    List<HintDTO> findHintsByTaskId(Long taskId);

}
