package no.bachelor26.Tasks.Hints;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import no.bachelor26.Tasks.Task;
import no.bachelor26.Tasks.DTO.TaskSeed;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;

@Service
public class HintService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired HintRepository hintRepo;

    @PersistenceContext EntityManager entityManager;

    public List<HintDTO> getTaskHints(Long taskID){
        return hintRepo.findHintsByTaskId(taskID);
    }



    /**
     * Initialiserer alle hintene til en gitt oppgave
     * 
     * @param seed Oppgavefrøet
     */
    public void createHints(TaskSeed seed){

        List<HintDTO> hints = seed.getHints();
        for(short hintIndex = 0; hintIndex < hints.size(); hintIndex++){
            HintDTO hint = hints.get(hintIndex);

            if(hintRepo.existsByTaskIdAndHintIndex(seed.getId(), Short.valueOf(hintIndex))){
                log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") finnes. Hopper over.");
                continue;
            }

            Hint newHint = new Hint();
            newHint.setHintMessage(hint.getHint());
            newHint.setHintIndex(Short.valueOf(hintIndex));
            newHint.setCost(hint.getCost());
            newHint.setTask(
                entityManager.getReference(Task.class, seed.getId())
            );

            hintRepo.save(newHint);
            log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") opprettet.");
        }


    }


    public void hardFlushAllHints(){
        hintRepo.deleteAll();
    }

}
