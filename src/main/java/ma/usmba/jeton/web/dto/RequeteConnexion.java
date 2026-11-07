package ma.usmba.jeton.web.dto;

import jakarta.validation.constraints.NotBlank;

public record RequeteConnexion(
        @NotBlank(message = "L'identifiant est obligatoire") String identifiant,
        @NotBlank(message = "Le mot de passe est obligatoire") String motDePasse) {
}
