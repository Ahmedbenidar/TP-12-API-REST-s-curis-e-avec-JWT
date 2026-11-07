package ma.usmba.jeton.config;

import ma.usmba.jeton.jeton.FiltreJetonJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class ConfigSecurite {

    private final FiltreJetonJwt filtreJeton;

    public ConfigSecurite(FiltreJetonJwt filtreJeton) {
        this.filtreJeton = filtreJeton;
    }

    @Bean
    public AuthenticationManager gestionnaireAuthentification(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain chaineFiltres(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                .requestMatchers("/", "/index.html", "/app.js", "/style.css", "/favicon.ico").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/administration/**").hasRole("ADMIN")
                .requestMatchers("/api/administration/**").hasRole("ADMIN")
                .requestMatchers("/api/membre/**").hasAnyRole("MEMBRE", "ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new PointEntreeNonAutorise())
                .accessDeniedHandler(new GestionnaireAccesRefuse())
            )
            .addFilterBefore(filtreJeton, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
