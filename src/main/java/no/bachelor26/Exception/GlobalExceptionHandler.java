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
                "cause", "Task not found",
                "desc", ex.getMessage()
            ));
    }


    // Når et brukernavn er i bruk
    @ExceptionHandler(UsernameTakenException.class)
    public ResponseEntity<?> handleUsernameTaken(UsernameTakenException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "cause", "Username taken",
                "desc", ex.getMessage()
            ));
    }


    // Når en epost er i bruk
    @ExceptionHandler(EmailInUseException.class)
    public ResponseEntity<?> handleEmailInUse(EmailInUseException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(
                "cause", "Email in use",
                "desc", ex.getMessage()
            ));
    }


    // Når en bruker ikke har tilgang til en oppgave
    @ExceptionHandler(NoTaskAccessException.class)
    public ResponseEntity<?> handleNoTaskAccess(NoTaskAccessException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(Map.of(
                "cause", "No task access",
                "desc", ex.getMessage()
            ));
    }


    // Når en bruker ikke har tilgang til en oppgave
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "cause", "Bad arguments",
                "desc", ex.getMessage()
            ));
    }

}
