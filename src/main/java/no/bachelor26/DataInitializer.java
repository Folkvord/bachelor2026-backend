package no.bachelor26;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import no.bachelor26.Tasks.TaskService;
import no.bachelor26.Tasks.DTO.TaskSeed;
import no.bachelor26.Tasks.Hints.HintService;
import no.bachelor26.User.UserService;
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
            } catch(IOException e){
                log.error("Kunne ikke parse json ;(");
                return;
            }

            taskService.createTask(taskSeed);
            hintService.createHints(taskSeed);

        }

    }


    
    /**
     * Initialiserer statiske brukere, som hovedadminbrukeren og utviklerenes brukere
     * 
     * @author Kristoffer Folkvord
     */
    private void initializeStaticUsers(){

        /* userService.createSpecialUser(
            "admin",
            "admin@admin.admin",
            "ikkehackmeg;(",
            User.Role.ADMIN
        ); */

    }

}
