package no.bachelor26.Tasks;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.bachelor26.Tasks.DTO.TaskComponents;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.JSON.TaskData;
import tools.jackson.databind.ObjectMapper;


/**
 * Klassen som prosesserer all oppgavedata før det sendes til klienten.
 * 
 * @author Kristoffer Folkvord
 */
@Component
public class TaskProcesser {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired ObjectMapper objectMapper;

    // Aksepterer alle ${**hva som helst**}, men ikke escaped variabler: \${bla bla}
    private static final Pattern VARIABLE_REGEX = Pattern.compile(
        "(?<!\\\\)\\$\\{([^}]+)\\}"
    );
    

    
    /**
     * Tar oppgavekomponentene og prosesserer dem.
     * 
     * @param taskComponents Oppgavekomponentene
     * @return De prosesserte oppgavekomponentene
     */
    public TaskComponents process(TaskComponents taskComponents){

        TaskData taskData = taskComponents.getData();
        taskData.setExtraDesc(
            processPart(taskData.getExtraDesc(), taskComponents.getFlag())
        );

        taskData.getData().forEach( (key, value) -> {
            // Hvis ikke en string, kan ikke være variabel
            if(!(value instanceof String)){
                return;
            }
            taskData.getData().replace(
                key, processPart(
                    (String) value,
                    taskComponents.getFlag()
                )
            );
        });

        List<HintDTO> hints = taskComponents.getHints();
        hints.forEach(hint -> {
            hint.setHint(
                processPart(hint.getHint(), taskComponents.getFlag())
            );
        });

        return taskComponents;
    }



    /**
     * Prosesserer en string; erstatter variabler ala: ${var-navn}. 
     * Ignorerer escaped variabler som: \${var-navn}.
     * 
     * @param part Stringen som prosesseres
     * @param flag Flagget oppgaven aksepterer (Erstatter ${flag})
     * @return Den prosesserte stringen
     */
    private String processPart(String part, String flag){

        Matcher matcher = VARIABLE_REGEX.matcher(part);
        StringBuffer result = new StringBuffer();

        while(matcher.find()){

            String variable = matcher.group().toLowerCase();

            if(variable.equals("${flag}")){
                matcher.appendReplacement(result, flag);
            }
            else if(variable.equals("${username}")){
                matcher.appendReplacement(result, "BRUKERNAVN :)");
            }
            else{
                log.warn("Ukjent oppgavevariabel funnet: " + variable);
            }

        }

        matcher.appendTail(result);
        return result.toString().replace("\\\\${", "${");
    }



}
