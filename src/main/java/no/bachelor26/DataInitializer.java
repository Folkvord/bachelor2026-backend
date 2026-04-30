package no.bachelor26;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import no.bachelor26.Tasks.TaskService;
import no.bachelor26.Tasks.DTO.TaskSeed;
import no.bachelor26.Tasks.Exception.TaskFileException;
import no.bachelor26.Tasks.Hints.HintService;
import no.bachelor26.User.UserService;
import no.bachelor26.User.User;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

/**
 * Klassen som initialiserer data ved hver oppstart
 * 
 * @author Kristoffer Folkvord
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    final private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired TaskService taskService;
    @Autowired HintService hintService;
    @Autowired UserService userService;
    @Autowired ObjectMapper objectMapper;
    @Autowired Validator validator;

    @Value("${data-initialization.hard-reset-tasks}")
    private boolean hardResetTasks = false;


    public void run(String... args){

        initializeTasks();
        initializeStaticUsers();

    }



    /**
     * Leser inn oppgavefilene fra resources/static/tasks/, og laster dem inn i DB-en.
     * Dersom en oppgave med ID-en gitt allerede eksisterer, skjer ingenting.
     */
    private void initializeTasks(){
        
        PathMatchingResourcePatternResolver resolver =
            new PathMatchingResourcePatternResolver();
        
        if(hardResetTasks){     // Må gjøres i denne rekkefølgen
            log.warn("Hardresetter alle oppgaver og hint.");
            hintService.hardFlushAllHints();
            taskService.hardFlushAllTasks();
        }

        Resource[] resources;
        try{
            resources = resolver.getResources("classpath:/static/tasks/*.json");
        } catch(IOException e){
            log.error("Kunne ikke finne oppgavefilene.");
            return;
        }

        for(Resource resource : resources){

            TaskSeed taskSeed;
            try(InputStream is = resource.getInputStream()){
                taskSeed = objectMapper.readValue(is, TaskSeed.class);
                validateTaskSeed(taskSeed);
            } catch(IOException e){
                log.error("Kunne ikke parse oppgavefil: " + resource.getFilename());
                continue;
            } catch(JacksonException e){
                log.error("Kunne ikke skape TaskSeed av oppgavefilen: " + resource.getFilename());
                continue;
            } catch(TaskFileException e){
                log.error("Oppgavefil er dårlig strukturert: " + resource.getFilename());
                continue;
            }

            // NB: Hint er avhengig av oppgaver, så oppgavene må skapes før hintene
            taskService.createTask(taskSeed);
            hintService.createHints(taskSeed);

        }

    }


    
    /**
     * Initialiserer statiske brukere, som hovedadminbrukeren og utviklerenes brukere
     */
    private void initializeStaticUsers(){
        
        userService.initializeStaticUsers(
            "admin",
            "admin@admin.no",
            "ikkehackmeg;(",
            User.Role.ADMIN
        );

        userService.initializeStaticUsers(  // Kristoffer
            "BIGSODA",
            "260562@usn.no",
            "${}",
            User.Role.DEV
        );

    }



    /**
     * Hjelpefunksjon for å validere om oppgavefrøet er gyldig
     * 
     * @param seed Oppgavefrøet
     * @throws TaskFileException
     */
    private void validateTaskSeed(TaskSeed seed) throws TaskFileException{

        Set<ConstraintViolation<TaskSeed>> violations = 
            validator.validate(seed);

        if(!violations.isEmpty()){
            throw new TaskFileException(violations.toString());
        }

    }

}
