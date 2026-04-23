package no.bachelor26.Tasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.bachelor26.Tasks.DTO.TaskProcessedResult;
import no.bachelor26.Tasks.JSON.TaskData;
import tools.jackson.databind.ObjectMapper;

@Component
public class TaskProcesser {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ObjectMapper objectMapper;


    static final Pattern VARIABLE_REGEX = Pattern.compile(
        "(?<!\\\\)\\$\\{([^}]+)\\}"
    );
    


    
    public TaskProcessedResult process(TaskComponents unprocessedTask){

        String flag = determineFlag(unprocessedTask);

        TaskData taskData = unprocessedTask.getTask();

        taskData.setExtraDesc(
            processPart(unprocessedTask.getTask().getExtraDesc(), flag)
        );

        taskData.getData().forEach( (k, v) -> {
            // Hvis ikke en string, kan ikke være variabel
            if(!(v instanceof String)){
                return;
            }

            taskData.getData().replace(
                k, processPart((String) v, flag)    // Vurder å legg til brukernavnet
            );

        });

        return new TaskProcessedResult(flag, taskData);

    }



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



    private String determineFlag(TaskComponents taskComponents){
        String flag = taskComponents.getStaticFlag();
        if(flag == null || flag.isBlank()){
            // Generer flagget
            flag = "CTF{ LOLL DETTE ER FLAGGET ASS }";
        }
        return flag;
    }



}
