package no.bachelor26.User;

import no.bachelor26.Tasks.TaskService;
import no.bachelor26.User.Exception.EmailInUseException;
import no.bachelor26.User.Exception.UsernameTakenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    TaskService taskService;

    @Autowired
    PasswordEncoder encoder;

    
    // TODO: Pass på at brukernavn og passord er gyldig formatert
    /**
     * Lager en ny bruker og lagrer den i databasen.
     * 
     * @param username Brukernavnet
     * @param email Eposten
     * @param password Passordet
     * @param passwordConfirm Passordet igjen
     * @author Sofie Berglund
     * @author Kristoffer Folkvord
     */
    public void registerUser(String username, String email, String password, String passwordConfirm){

        if (!password.equals(passwordConfirm)){
            throw new IllegalArgumentException("Password");
        }

        if (userRepo.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new EmailInUseException(email);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password));

        // HUSK Å BEKREFT EPOSTEN!!!!
        user.setEmail(email);

        //grantUserIntroTasks(user);

        userRepo.save(user);

    }


/*     // Gir en bruker tilgang til de 9 introoppgavene
    private void grantUserIntroTasks(User user){

        for(int i = 1; i <= 9; i++){
            Long taskID = Long.valueOf(i);
            taskService.grantTaskAccess(user, taskID);
        }

    } */

}