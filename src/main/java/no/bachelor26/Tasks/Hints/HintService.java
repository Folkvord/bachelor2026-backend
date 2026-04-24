package no.bachelor26.Tasks.Hints;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.Hints.DTO.HintDTO;

@Service
public class HintService {

    @Autowired HintRepository hintRepo;



    public List<HintDTO> getTaskHints(Long taskID){
        return hintRepo.findHintsByTaskId(taskID);
    }

}
