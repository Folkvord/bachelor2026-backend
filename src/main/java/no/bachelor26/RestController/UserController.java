package no.bachelor26.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.bachelor26.DTO.RegisterUserDTO;

/* 
 * Controlleren for all brukerrelaterte API-er
 * 
 * For at API-kallet skal fungere som forventet, kreves det 
 * at JSON-objektet som sendes har alle de samme attributtene
 * som objektet i parameteret. 
 * 
 * !!! RETURNERER 501 RN, ENDRE ETTER IMPLEMENTASJON !!!
 */

@RestController
@RequestMapping("/api/user")
public class UserController {

    //@Autowired
    //UserService userService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserDTO registrationFormDTO){
        
        // Kristoffer: Sofies ansvar!!!
        //userService.register(registrationFormDTO);

        // Kristoffer: Endre ettersom implementert
        return ResponseEntity.status(501).build();

    }

}
