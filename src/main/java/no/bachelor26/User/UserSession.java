package no.bachelor26.User;

import java.util.UUID;

import lombok.Data;

@Data
public class UserSession {
    
    private UserState state = UserState.IDLE;
    private UUID userID;

    public UserSession(UUID userID){
        this.userID = userID;
    }

    public String toString(){
        return "UserID: (" + userID + "), State: (" + state + ")";
    }

}
