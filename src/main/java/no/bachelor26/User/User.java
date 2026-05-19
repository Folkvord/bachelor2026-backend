package no.bachelor26.User;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;

/**
 * Brukerentitet for brukertabellen.
 * Representeren én bruker i systemet. 
 * 
 * Ansvar:
 * Lagre brukerinformasjon
 * Autentisering 
 * 
 * @author Sofie Emmelin Weber 
 */

@Entity
@Data
@Table(name="users")

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable=false,unique=true, length = 25)
    private String username; 

    @Column(nullable=false,unique=true, length = 100)
    private String email;

    @Column(nullable=false, length = 100)
    private String passwordHash;

    // SOFIE: rolle kan ikke oppdateres (sikkerhetsaspekt), kan ikke være tom
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, updatable=false)
    private Role role = Role.STUDENT;
    
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public enum Role {
        STUDENT,
        ADMIN,
        DEV
    }
    


    @PrePersist
    protected void onCreate() {
    Instant now = Instant.now();

    createdAt = now;
    updatedAt = now;

}
    @PreUpdate
    protected void onUpdate() {
    updatedAt = Instant.now();

}

}
