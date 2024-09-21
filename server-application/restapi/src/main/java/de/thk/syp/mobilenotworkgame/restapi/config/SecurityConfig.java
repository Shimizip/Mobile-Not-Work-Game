package de.thk.syp.mobilenotworkgame.restapi.config;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RsaKeyProperties rsaKeys;

    public SecurityConfig(RsaKeyProperties rsaKeys) {
        this.rsaKeys = rsaKeys;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // disable cross site request forgery protection, WICHTIG: session management muss dazu auch disabled sein
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/").permitAll()
                        .requestMatchers("/spieler/login").permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/spieler/register").permitAll()
                        .requestMatchers("/spieler*").hasRole("ADMIN")
                        .requestMatchers("/spieler/freischalten/**").hasRole("ADMIN")
                        .requestMatchers("/spieler/blockieren/**").hasRole("ADMIN")
                        .requestMatchers("/spieler/benutzername-aendern/**").hasAnyRole("ADMIN", "SPIELER")
                        .requestMatchers("/spieler/benutzername/**").hasAnyRole("ADMIN", "SPIELER")
                        .requestMatchers("/messung*").hasRole("SPIELER")
                        .requestMatchers("/spiel/**").hasAnyRole("ADMIN", "SPIELER")
                        .requestMatchers("/karte/get-ksid-fuer-standort/**").hasAnyRole("ADMIN", "SPIELER")
                        .requestMatchers("/karte/get-kartenbereich-daten/spieler-ansicht**").hasRole("SPIELER")
                        .requestMatchers("/karte/get-kartenbereich-daten/admin-ansicht**").hasRole("ADMIN")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //stateless, da nur REST-API
                .httpBasic(Customizer.withDefaults()) // Auth fuer Login
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("*")); // Erlaubt alle Origins
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Erlaubt spezifische HTTP-Methoden
                    config.setAllowedHeaders(Arrays.asList("*")); // Erlaubt alle Headers
                    return config;
                }))
                .build();
    }

    @Bean
    JwtDecoder jwtDecoder () {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.publicKey()).build();
    }

    @Bean
    JwtEncoder jwtEncoder () {
        JWK jwk = new RSAKey.Builder(rsaKeys.publicKey()).privateKey(rsaKeys.privateKey()).build();
        JWKSource<SecurityContext> jkws = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jkws);
    }
}