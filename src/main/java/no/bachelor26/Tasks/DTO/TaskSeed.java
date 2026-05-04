package no.bachelor26.Tasks.DTO;

import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.JSON.TaskData;

/**
 * En klasse som representerer et frø skapt fra en oppgavefil.
 * Brukes av datainitilaisereren.
 */
@Getter
public class TaskSeed {
    
    @NotNull
    private Long id;

    @Nullable
    private Long unlocks;

    @Nullable
    private String staticFlag;

    @Valid
    @NotNull
    private TaskData taskData;

    @NotNull
    private List<HintDTO> hints;

}
