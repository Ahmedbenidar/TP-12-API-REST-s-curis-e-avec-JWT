package ma.usmba.jeton.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequeteInscription(
        @NotBlank(message = "L'identifiant est obligatoire")
        @Size(min = 3, max = 60, message = "L'identifiant doit contenir entre 3 et 60 caracteres")
        String identifiant,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 5, max = 100, message = "Le mot de passe doit contenir au moins 5 caracteres")
        String motDePasse,

        @NotBlank(message = "Le nom complet est obligatoire")
        String nomComplet,

        @Email(message = "L'adresse email n'est pas valide")
        String email) {
}
