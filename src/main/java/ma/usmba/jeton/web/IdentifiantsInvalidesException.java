package ma.usmba.jeton.web;

public class IdentifiantsInvalidesException extends RuntimeException {

    public IdentifiantsInvalidesException() {
        super("Identifiant ou mot de passe incorrect");
    }
}
