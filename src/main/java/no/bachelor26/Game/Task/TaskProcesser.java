package no.bachelor26.Game.Task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import no.bachelor26.Projection.TaskContent;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class TaskProcesser {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ObjectMapper objectMapper;


    private final String TEST_FLAG = "CTF{NICE FLAGG ;))}";

    static final Pattern VARIABLE_REGEX = Pattern.compile(
        "(?<!\\\\)\\$\\{([^}]+)\\}"
    );
    

    public JsonNode preprocess(TaskContent unprocessedTask){

        String flag = unprocessedTask.getStaticFlag();
        if(flag == null){
            //flag = flagService.generateFlag();
            flag = TEST_FLAG;
        }

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
        return objectMapper.readTree(result.toString().replace("\\\\${", "${"));

    }
    
}
