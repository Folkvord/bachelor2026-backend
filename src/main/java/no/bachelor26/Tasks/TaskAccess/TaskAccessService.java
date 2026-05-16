package no.bachelor26.Tasks.TaskAccess;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.TaskRepository;
import no.bachelor26.User.User;
import no.bachelor26.User.UserSessions.UserSession;

/**
 * Klassen som overser brukeres tilgang til oppgaver.
 * Eies av TaskService
 * 
 * @author Kristoffer Folkvord
 */
@Service
public class TaskAccessService {

    @Autowired TaskAccessRepository taskAccessRepo;
    @Autowired TaskRepository taskRepo;

    private static final Integer FIRST_TASK_ID = Integer.valueOf(1);



    /**
     * Sjekker om en bruker har tilgang til en oppgave.
     * 
     * @param user Brukersesjonsobjektet
     * @param taskID OppgaveID-en
     * @return {@code boolean} du er dum om du ikke vet
     */
    public boolean userHasAccess(UserSession user, Integer taskID){
        if(userHasSpecialAccess(user.getRole())){
            return true;
        }
        return taskAccessRepo.existsByIdUserIDAndIdTaskID(user.getUserID(), taskID);
    }


    
    /**
     * Henter en liste over ID-ene til oppgavene
     * som er tilgjengelig for spilleren. 
     * 
     * @param user Brukersesjonsobjektet
     * @return {@code List<Integer>} over tilgjengelige oppgave-IDer
     */
    public List<Integer> getAvailableTasks(UserSession user){
        if(userHasSpecialAccess(user.getRole())){
            return taskRepo.findAllIDs();
        }
        return taskAccessRepo.findAllAccessableTasks(user.getUserID());
    }


    /**
     * Gir brukeren tilgang til en gitt oppgave-ID.
     * 
     * @param userID BrukerID-en
     * @param taskID OppgaveID-en
     */
    public void grantUserAccess(Integer userID, Integer taskID){
        if(userHasAccess(userID, taskID)){
            return;
        }

        TaskAccess accessToken = new TaskAccess(userID, taskID);
        taskAccessRepo.save(accessToken);
    }

    

    /**
     * Gir en ny bruker tilgang til den første oppgaven.
     * 
     * @param userID BrukerID-en
     */
    public void grantNewUserAccess(Integer userID){
        TaskAccess accessToken = new TaskAccess(userID, FIRST_TASK_ID);
        taskAccessRepo.save(accessToken);
    }





    /**
     * Sjekker om en bruker har en rolle 
     * som påvirker oppgavetilgjengeligheten
     * SKAL FLYTTES UT NÅR ROLLER UTVIKLES
     * 
     * @param role Brukerrollen
     * @return {@code boolean} påvirkes
     */
    private boolean userHasSpecialAccess(User.Role role){
        return role == User.Role.ADMIN ||
               role == User.Role.DEV;
    }



    // Enklere, privat versjon av tilgangsjekken
    private boolean userHasAccess(Integer userID, Integer taskID){
        return taskAccessRepo.existsByIdUserIDAndIdTaskID(userID, taskID);
    }

}
