package no.bachelor26.Tasks.TaskAccess;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.TaskRepository;
import no.bachelor26.User.User;
import no.bachelor26.User.UserSessions.UserSession;

@Service
public class TaskAccessService {

    @Autowired TaskAccessRepository taskAccessRepo;
    @Autowired TaskRepository taskRepo;
    


    public boolean userHasAccess(UUID userID, Long taskID){
        return taskAccessRepo.existsByIdUserIDAndTaskId(userID, taskID);
    }


    
    public boolean userHasAccess(UserSession user, Long taskID){
        if(userHasSpecialAccess(user.getRole())){
            return true;
        }
        return taskAccessRepo.existsByIdUserIDAndTaskId(user.getUserID(), taskID);
    }


    
    public List<Long> getAvailableTasks(UserSession user){
        if(userHasSpecialAccess(user.getRole())){
            return taskRepo.findAllIDs();
        }
        return taskAccessRepo.findAllAccessableTasks(user.getUserID());
    }


    /**
     * Gir brukeren tilgang til en oppgave
     * 
     * @param userID
     * @param taskID
     */
    public void grantUserAccess(UUID userID, Long taskID){
        if(userHasAccess(userID, taskID)){
            return;
        }

        TaskAccess accessToken = new TaskAccess(userID, taskID);
        taskAccessRepo.save(accessToken);
    }
    


    private boolean userHasSpecialAccess(User.Role role){
        return role == User.Role.ADMIN ||
               role == User.Role.DEV;
    }

}
