package no.bachelor26.Entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;

// SOFIE: markerer User klassen som en JPA entitet
@Entity
// SOFIE: Settere og gettere
@Data
@Table(name="users")

public class User {
    // SOFIE: Designerer id som primærnøkkel
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    // SOFIE: brukernavn kan ikke være tom, må være unik, makslengde 25 tegn
    @Column(nullable=false,unique=true, length = 25)
    private String username; 

    // SOFIE: email kan ikke være tom, må være unik, makslengde 100 tegn
    @Column(nullable=false,unique=true, length = 100)
    private String email;

    // SOFIE: passord lagres i hashformet, og kan ikke være null, 
    // og makslengde på 100 tegn
    @Column(nullable=false, length = 100)
    private String passwordHash;

    // SOFIE: rolle kan ikke oppdateres (sikkerhetsaspekt), kan ikke være tom
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, updatable=false)
    private Role role;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum Role {
        STUDENT,
        ADMIN,
        DEV
    }
    


}
