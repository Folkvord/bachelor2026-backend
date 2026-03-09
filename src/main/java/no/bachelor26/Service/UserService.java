package no.bachelor26.Service;

import no.bachelor26.Entity.User;
import no.bachelor26.Exception.EmailInUseException;
import no.bachelor26.Exception.UsernameTakenException;
import no.bachelor26.Repository.UserRepository;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    TaskService taskService;

    //@Autowired
    //PasswordEncoder encoder;


    public User getUserFromId(UUID userID){
        return userRepo.findById(userID).orElseThrow(
            () -> new UsernameNotFoundException(userID.toString())
        );
    }


    public void registerUser(String username, String password, String email){

        if (userRepo.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new EmailInUseException(email);
        }

        User user = new User();
        user.setUsername(username);
        //user.setPasswordHash(encoder.encode(password));
        // HUSK Å BEKREFT EPOSTEN!!!!
        user.setEmail(email);

        grantUserIntroTasks(user);

    }


    // Gir en bruker tilgang til de 9 introoppgavene
    private void grantUserIntroTasks(User user){

        for(int i = 1; i <= 9; i++){
            Long taskID = Long.valueOf(i);
            taskService.grantTaskAccess(user, taskID);
        }

    }

}