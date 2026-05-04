package no.bachelor26.User;

import no.bachelor26.Tasks.TaskService;
import no.bachelor26.Tasks.TaskAccess.TaskAccessService;
import no.bachelor26.User.Exception.EmailInUseException;
import no.bachelor26.User.Exception.UsernameTakenException;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired UserRepository userRepo;
    @Autowired TaskService taskService;
    @Autowired TaskAccessService taskAccessService;
    @Autowired PasswordEncoder encoder;

    
    
    public User getUserByID(UUID userID){
        return userRepo.findById(userID).orElseThrow();
    }

    public User getUserByName(String name){
        return userRepo.findByUsername(name).orElseThrow();
    }



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



    /**
     * Lager en bruker uten sjekker som {@code registerUser()}
     * 
     * @param username Brukernavnet
     * @param email Eposten
     * @param password Passordet
     * @author Kristoffer Folkvord
     */
    public void initializeStaticUsers(String username, String email, String password, User.Role role){

        if(userRepo.existsByUsername(username)){
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(encoder.encode(password));
        user.setRole(role);
        userRepo.save(user);
        
        System.out.println(user.getId());
        taskAccessService.grantNewUserAccess(user.getId());
    }

}