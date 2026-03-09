package no.bachelor26.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Entity.User;
import no.bachelor26.Projection.TaskInfo;
import no.bachelor26.Repository.AvailableTaskRepository;

@Service
public class AvailableTaskService {

    @Autowired
    AvailableTaskRepository availableTaskRepo;


    /**
     * Sjekker om en bruker har tilgang til en oppgave.
     * 
     * @param user Brukeren
     * @param taskID ID-en til oppgaven
     * @return 
     */
    public boolean userHasAccessToTask(User user, Long taskID){
        return availableTaskRepo.existsByUserIdAndTaskId(user.getId(), taskID);
    }

    /**
     * Gir en liste med id-en, navnet og beskrivelsen til 
     * alle tilgjengelige oppgaver.
     * 
     * @param user Brukeren
     * @return Liste med oppgaveinfo
     */
    public List<TaskInfo> getAvailableTaskInfo(User user){
        return availableTaskRepo.findInfoByUserId(user.getId());
    }

}
