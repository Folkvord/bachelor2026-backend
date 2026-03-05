package no.bachelor26.RestController;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.bachelor26.DTO.FlagValidationDTO;


/* 
 * Controlleren for flagg
 * 
 * !!! ALLE FUNKSJONER RETURNERER 501 RN
 * FJERN ETTER SOM METODENE BLIR IMPLEMENTER !!!
 */

@RestController
@RequestMapping("/api/flag")
public class FlagController {

    // @AutoWired
    // FlagService flagService;


    @GetMapping("")
    public ResponseEntity<?> getFlag(){

        // Kristoffer: Generer flagg
        //String flag = flagService.generateFlagg();
        
        // Kristoffer: Endre når implementert
        return ResponseEntity.status(501).build();

    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateFlag(@RequestBody FlagValidationDTO flagDTO){

        // Kristoffer: Valider flagg
        //boolean flagIsValid = flagService.validateFlag(flagDTO.getFlag(), flagDTO.getUserId());

        //String response = flagIsValid ? "yea" : "naw";
        // return ResponseEntity.ok().body(response);

        // Kristoffer: Fjern når implementert
        return ResponseEntity.status(501).build();

    }

}
