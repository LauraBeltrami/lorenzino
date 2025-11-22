package org.example.ids.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disabilita CSRF (Fondamentale per le chiamate API REST/Postman)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configura le regole di accesso (Autorizzazione)
                .authorizeHttpRequests(auth -> auth
                        // Animatori
                        .requestMatchers("/api/animatori/**").hasRole("ANIMATORE")

                        // Venditori (e Distributori)
                        .requestMatchers("/api/venditori/**").hasRole("VENDITORE")
                        .requestMatchers("/api/distributori/**").hasRole("VENDITORE") // Se usi distributori

                        // Acquirenti
                        .requestMatchers("/api/acquirenti/**").hasRole("ACQUIRENTE")

                        // Curatori (se hai controller per loro)
                        .requestMatchers("/api/curatori/**").hasRole("CURATORE")

                        // Eventi: Lettura pubblica (GET), Scrittura protetta (se servisse)
                        .requestMatchers(HttpMethod.GET, "/api/eventi/**").permitAll() // Tutti possono vedere gli eventi

                        // H2 Console (se la usi, serve per permettere l'accesso)
                        .requestMatchers("/h2-console/**").permitAll()

                        // Tutto il resto richiede autenticazione
                        .anyRequest().authenticated()
                )

                // 3. Abilita Basic Auth
                .httpBasic(Customizer.withDefaults())

                // 4. Fix per H2 Console (Spring Security blocca i frame di default)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        // Definiamo utenti finti per testare i ruoli

        UserDetails animatore = User.builder()
                .username("animatore")
                .password(passwordEncoder().encode("pass")) // password: pass
                .roles("ANIMATORE")
                .build();

        UserDetails venditore = User.builder()
                .username("venditore")
                .password(passwordEncoder().encode("pass"))
                .roles("VENDITORE")
                .build();

        UserDetails acquirente = User.builder()
                .username("acquirente")
                .password(passwordEncoder().encode("pass"))
                .roles("ACQUIRENTE")
                .build();

        UserDetails curatore = User.builder()
                .username("curatore")
                .password(passwordEncoder().encode("pass"))
                .roles("CURATORE")
                .build();

        UserDetails admin = User.builder() // Un super utente che può fare tutto se configuri i ruoli
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ANIMATORE", "VENDITORE", "ACQUIRENTE", "CURATORE")
                .build();

        return new InMemoryUserDetailsManager(animatore, venditore, acquirente, curatore, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}