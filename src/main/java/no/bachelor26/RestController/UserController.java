package no.bachelor26.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import no.bachelor26.DTO.RegisterUserDTO;
import no.bachelor26.Service.UserService;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    private final Logger log = LoggerFactory.getLogger(UserController.class);


    @PostMapping("/login")
    public ResponseEntity<?> login(){

        

        return ResponseEntity.status(501).build();
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDTO registrationFormDTO){

        log.info("LAGER BRUKER");

        userService.registerUser(
            registrationFormDTO.getUsername(),
            registrationFormDTO.getEmail(),
            registrationFormDTO.getPassword(),
            registrationFormDTO.getPasswordConfirm()
        );

        return ResponseEntity.status(201).build();

    }

}
