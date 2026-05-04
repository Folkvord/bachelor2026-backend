package no.bachelor26.Tasks.TaskSessions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import no.bachelor26.Tasks.DTO.TaskComponents;
import no.bachelor26.Tasks.Hints.DTO.HintDTO;
import no.bachelor26.Tasks.Hints.DTO.HintResult;

/**
 * Service-klassen som har ansvar for alle de aktive oppgavesesjonene.
 * 
 * @author Kristoffer Folkvord
 */
@Service
public class TaskSessionService {

    private Map<UUID, TaskSession> activeSessions = 
        new ConcurrentHashMap<>();


    /**
     * Starter en oppgavesesjon for en gitt bruker
     * 
     * @param userID BrukerID-en
     * @param taskID OppgaveID-en
     * @param flag Det flagget oppgaven kommer til å akseptere
     * @param hints Listen over hintene brukeren kan få
     */
    public boolean startTaskSession(
        UUID userID, Long taskID, 
        TaskComponents taskComponents
    ){
        if(userInActiveSession(userID)){
            return false;
        }
        
        TaskSession session = new TaskSession(userID, taskID, taskComponents);
        activeSessions.put(userID, session);
        return true;
    }



    // MIDLERTIDIG FLAGGVALIDERING //
    public boolean validateFlag(UUID userID, String guessedFlag){
        String sessionFlag = getTaskSession(userID).getFlag();
        return guessedFlag.equals(sessionFlag);
    }



    /**
     * Henter hintet som en {@code HintResult}.
     * Failer dersom hintet ikke eksisterer eller om det har blitt hentet før
     * 
     * @param index Indeksen på hintet
     * @return {@code HintResult} med en statusmelding og et hint om alt ok
     */
    public HintResult retrieveHint(UUID userID, int hintIndex){
        List<HintDTO> hints = getTaskSession(userID).getHints();
        Optional<HintDTO> possibleHint = hints.stream()
            .filter(h -> h.getIndex() == hintIndex-1)   // -1 mens indeksen bestemmes
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

    
    
    /**
     * Fullfører oppgaven 
     * 
     * @param userID BrukerID-en
     * @return OppgaveID-en til oppgaven som låses opp; kan være {@code null}
     */
    public Long completeSession(UUID userID){
        if(!userInActiveSession(userID)){
            return null;
        }

        TaskSession session = getTaskSession(userID);
        
        // Logg som fullført oppgave

        activeSessions.remove(userID);
        return session.getUnlocksTaskID();
    }


    public void cancelSession(UUID userID){
        activeSessions.remove(userID);
    }

    public boolean userInActiveSession(UUID userID){
        return activeSessions.containsKey(userID);
    }

    private TaskSession getTaskSession(UUID userID){
        return activeSessions.get(userID);
    }
    
}
