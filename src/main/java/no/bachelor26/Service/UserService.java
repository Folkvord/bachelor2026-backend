package no.bachelor26.Service;

import no.bachelor26.Entity.User;
import no.bachelor26.Exception.EmailInUseException;
import no.bachelor26.Exception.UsernameTakenException;
import no.bachelor26.Repository.UserRepository;

import java.beans.Encoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;


    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already in use");
        }
        return userRepository.save(user);
    }

    public void registerUser(String username, String password, String email){

        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException(username);
        }
        if (userRepository.existsByEmail(email)) {
            throw new EmailInUseException(email);
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(password));
        
        // HUSK Å BEKREFT EPOSTEN!!!!
        user.setEmail(email);

        

    }

}