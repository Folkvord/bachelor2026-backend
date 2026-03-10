package no.bachelor26.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import no.bachelor26.Entity.User;
import no.bachelor26.Repository.UserRepository;

@Component
public class UserTranslator implements UserDetailsService {
    
    @Autowired
    UserRepository userRepo;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepo.findByUsername(username).orElseThrow(
            () -> new UsernameNotFoundException(username)
        );

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPasswordHash())
            .roles(user.getRole().toString())
            .build();
        
    }

}
