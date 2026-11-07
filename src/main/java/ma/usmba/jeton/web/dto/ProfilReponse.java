package ma.usmba.jeton.web.dto;

public record ProfilReponse(
        Long id,
        String identifiant,
        String nomComplet,
        String email,
        boolean actif,
        String dateInscription,
        java.util.List<String> roles) {
}
