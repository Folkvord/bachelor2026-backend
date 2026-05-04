package no.bachelor26.User.DTO;

import java.util.UUID;

import lombok.Data;
import no.bachelor26.User.User;

@Data
public class UserDTO {
    
    private UUID id;
    private String username;
    private User.Role role;

    public UserDTO(User user){
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
    }

}
