package no.bachelor26.User.UserSessions;

import java.util.UUID;

import lombok.Data;
import no.bachelor26.User.User;

@Data
public class UserSession {
    
    private UserState state = UserState.IDLE;
    private User.Role role;
    private UUID userID;

    public UserSession(User user){
        userID = user.getId();
        role = user.getRole();
    }

    public String toString(){
        return "UserID: (" + userID + "), State: (" + state + ")";
    }

}
