package no.bachelor26.Tasks.JSON;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Klassen som representerer oppgavens JSON-data
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class TaskData {

    @NotNull
    private String extraDesc;

    @NotNull
    private Map<String, Object> data;

}
