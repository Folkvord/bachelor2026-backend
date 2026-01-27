package no.bachelor26.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FlagValidationDTO {
    
    @NotNull
    @NotBlank
    private Long userId;

    @NotNull
    private String flag;

}
