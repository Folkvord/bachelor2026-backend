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

    // SOFIE: brukernavn kan ikke være tom, må være unik
    @Column(nullable=false,unique=true)
    private String username; 

    // SOFIE: email kan ikke være tom, må være unik
    @Column(nullable=false,unique=true)
    private String email;

    // SOFIE: passord lagres i hash, og kan ikke være null
    @Column(nullable=false)
    private String passwordHash;

    // SOFIE: rolle kan ikke oppdateres, kan ikke være tom
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, updatable=false)
    private Role role;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public enum Role {
        STUDENT,
        ADMIN
    }
    


}
