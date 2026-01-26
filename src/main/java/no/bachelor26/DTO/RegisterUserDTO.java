package no.bachelor26.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import no.bachelor26.Annotation.FieldMatch;

@Data
@FieldMatch(first = "password", second = "passwordConfirm")
public class RegisterUserDTO {

    @NotNull
    @Size(min = 5, max = 20)
    private String username;

    @NotNull    // Kristoffer: mulig defekt; snakker ikke regex.
    @Email(regexp = "^[a-zA-Z0-9_+&*-] + (?:\\\\.[a-zA-Z0-9_+&*-] + )*@(?:[a-zA-Z0-9-]+\\\\.) + [a-zA-Z]{2, 7}")
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String passwordConfirm;

}
