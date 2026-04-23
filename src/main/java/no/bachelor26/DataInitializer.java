package no.bachelor26;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import no.bachelor26.Tasks.TaskService;

/**
 * Klassen som initialiserer data ved hver oppstart
 * 
 * @author Kristoffer Folkvord
 */
public class DataInitializer implements CommandLineRunner {
    
    @Autowired TaskService taskService;



    public void run(String... args){

        taskService.createTask(null, null, null);

    }

}
