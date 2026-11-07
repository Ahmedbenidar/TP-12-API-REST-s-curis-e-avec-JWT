package ma.usmba.jeton.jeton;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jeton")
public record ProprietesJeton(String secret, long dureeValidite, String emetteur) {
}
