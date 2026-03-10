package no.bachelor26.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Projection.TaskInfo;
import no.bachelor26.Repository.AvailableTaskRepository;

@Service
public class AvailableTaskService {

    @Autowired
    AvailableTaskRepository availableTaskRepo;


    /**
     * Sjekker om en bruker har tilgang til en oppgave.
     * 
     * @param userID Brukeren
     * @param taskID ID-en til oppgaven
     * @return 
     */
    public boolean userHasAccessToTask(UUID userID, Long taskID){
        return availableTaskRepo.existsByUserIdAndTaskId(userID, taskID);
    }


    /**
     * Gir en liste med id-en, navnet og beskrivelsen til 
     * alle tilgjengelige oppgaver.
     * 
     * @param userID Brukerens ID
     * @return Liste med oppgaveinfo
     */
    public List<TaskInfo> getAvailableTaskInfo(UUID userID){
        return availableTaskRepo.findInfoByUserId(userID);
    }


}
