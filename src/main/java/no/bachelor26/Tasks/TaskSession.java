package no.bachelor26.Tasks;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.Data;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.Hints.DTO.HintResult;

@Data
public class TaskSession {

    private UUID userID;
    private Long taskID;
    private LocalTime taskStart;

    private String flag;
    private List<HintDTO> hints;


    public TaskSession(
        UUID userID,
        Long taskID,
        String flag,
        List<HintDTO> hints
    ){
        this.userID = userID;
        this.taskID = taskID;
        this.flag = flag;
        this.hints = hints;
        taskStart = LocalTime.now();
    }



    // Midlertidig validering
    public boolean validateFlag(String guessedFlag){
        return guessedFlag.equals(this.flag);
    }



    /**
     * Henter hintet som en {@code Optional<String>}.
     * Failer dersom hintet ikke eksisterer eller om det har blitt hentet før
     * 
     * @param index Indeksen på hintet
     * @return {@code Optional<String>} med eller uten hintet
     */
    public HintResult getHint(int index){
        Optional<HintDTO> possibleHint = hints.stream()
            .filter(h -> h.getIndex() == index-1)
            .findFirst();

        HintResult result = new HintResult();

        // Hvis dette skjer, kan det hende at 
        // brukeren prøver å hacke oss!!!
        if(possibleHint.isEmpty()){
            result.setStatus(HintResult.Status.INVALID_HINT);
            return result;
        }

        HintDTO hint = possibleHint.get();
        if(hint.isRetrieved()){
            result.setStatus(HintResult.Status.RETRIEVED);
            return result;
        }

        hint.setRetrieved(true);

        // Påvirk statistikk / poeng / whatever

        result.setStatus(HintResult.Status.OK);
        result.setHint(hint.getHint());

        return result;
    }


}


