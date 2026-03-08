package no.bachelor26.Exception;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    
    // Når en oppgave ikke finnes.
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<?> handleTaskNotFound(TaskNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "error", "NOT-FOUND",
                "msg", ex.getMessage()
            ));
    }

    // Når et brukernavn er i bruk
    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<?> handleUsernameTaken(UsernameTakenException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "Error", "Taken",
                "msg", ex.getMessage()
            ));
    }

    // Når en epost er i bruk
    @ExceptionHandler(EmailInUseException.class)
    public ResponseEntity<?> handleEmailInUse(EmailInUseException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(
                "Error", "In use",
                "msg", ex.getMessage()
            ));
    }

}
