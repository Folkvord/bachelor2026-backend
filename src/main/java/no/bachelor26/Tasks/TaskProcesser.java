package no.bachelor26.Tasks;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.bachelor26.Tasks.DTO.TaskProcessedResult;
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

        Matcher matcher = VARIABLE_REGEX.matcher(unprocessedTask.getTask());
        StringBuffer result = new StringBuffer();

        while(matcher.find()){

            String variable = matcher.group().toLowerCase();

            if(variable.equals("${flag}")){
                matcher.appendReplacement(result, flag);
            }
            else if(variable.equals("${username}")){
                matcher.appendReplacement(result, "BALLEFANT");
            }
            else{
                log.warn("Ukjent oppgavevariabel funnet: " + variable);
            }

        }

        matcher.appendTail(result);
        
        return new TaskProcessedResult(
            flag,
            objectMapper.readTree(result.toString().replace("\\\\${", "${"))
        );

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
