package no.bachelor26.User.UserSessions;

import java.util.UUID;

import lombok.Data;
import no.bachelor26.User.User;

@Data
public class UserSession {
    
    private UserState state = UserState.IDLE;
    private String username;
    private User.Role role;
    private UUID userID;

    public UserSession(User user){
        userID = user.getId();
        username = user.getUsername();
        role = user.getRole();
    }

    public void changeState(UserState state){
        System.out.println("(" + username  + ") TILSTAND ENDRET TIL: " + state.name());
        this.state = state;
    }

    public String toString(){
        return "UserID: (" + userID + "), State: (" + state + ")";
    }

}
