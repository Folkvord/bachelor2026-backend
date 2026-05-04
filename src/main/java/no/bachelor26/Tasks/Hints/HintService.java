package no.bachelor26.Tasks.Hints;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

            if(hintRepo.existsByIdTaskIDAndIdIndex(seed.getId(), Short.valueOf(hintIndex))){
                log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") finnes. Hopper over.");
                continue;
            }

            Hint newHint = new Hint(seed.getId(), Short.valueOf(hintIndex));
            newHint.setHintMessage(hint.getHint());
            newHint.setCost(hint.getCost());
/*             newHint.setTask(
                entityManager.getReference(Task.class, seed.getId())
            ); */

            hintRepo.save(newHint);
            log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") opprettet.");
        }

    }



    /**
     * Initialiserer eller redigerer alle hintene til en gitt oppgave
     * utifra oppgavefrøet
     * 
     * @param seed Oppgavefrøet
     */
    public void editOrCreateHints(TaskSeed seed, boolean skipIfPresent){
        List<HintDTO> hints = seed.getHints();
        for(short hintIndex = 0; hintIndex < hints.size(); hintIndex++){
            HintDTO hintDTO = hints.get(hintIndex);

            HintId id = new HintId(seed.getId(), hintIndex);
            Optional<Hint> possibleHint = hintRepo.findById(id);
            
            Hint hint;
            String actionTaken = "none";
            if(possibleHint.isPresent() && skipIfPresent){
                log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") finnes. Hopper over.");
                continue;
            }
            else if(possibleHint.isPresent()){
                hint = possibleHint.get();
                actionTaken = "redigert.";
            }
            else{
                hint = new Hint(id);
                actionTaken = "opprettet.";
            }

            hint.setCost(hintDTO.getCost());
            hint.setHintMessage(hintDTO.getHint());

            hintRepo.save(hint);
            log.info("OppgaveID (" + seed.getId() + ") - hintindeks (" + hintIndex + ") " + actionTaken);
        }

    }



    public void hardFlushAllHints(){
        hintRepo.deleteAll();
    }

}
