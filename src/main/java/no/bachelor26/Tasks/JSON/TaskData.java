package no.bachelor26.Tasks.JSON;

import java.util.Map;

import lombok.Data;

/**
 * Klassen som representerer oppgavens JSON-data
 * 
 * @author Kristoffer Folkvord
 */
@Data
public class TaskData {

    private String extraDesc;
    private Map<String, Object> data;

}
