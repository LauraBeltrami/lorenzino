package org.example.ids.config;

import org.example.ids.Model.*;
import org.example.ids.Repository.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AcquirenteRepository acquirenteRepo;
    private final VenditoreRepository venditoreRepo;
    private final AnimatoreRepository animatoreRepo;
    private final CuratoreRepository curatoreRepo;

    public CustomUserDetailsService(AcquirenteRepository acquirenteRepo,
                                    VenditoreRepository venditoreRepo,
                                    AnimatoreRepository animatoreRepo,
                                    CuratoreRepository curatoreRepo) {
        this.acquirenteRepo = acquirenteRepo;
        this.venditoreRepo = venditoreRepo;
        this.animatoreRepo = animatoreRepo;
        this.curatoreRepo = curatoreRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Strategia "Brute Force": Cerca in tutte le tabelle finché non lo trovi.
        // Nota: Assumiamo che l'email sia univoca in tutto il sistema.

        // 1. Cerca Acquirente
        Optional<Acquirente> acq = acquirenteRepo.findByEmail(email);
        if (acq.isPresent()) return buildUser(acq.get());

        // 2. Cerca Venditore (devi aggiungere findByEmail nel repo se non c'è)
        Optional<Venditore> ven = venditoreRepo.findByEmail(email);
        if (ven.isPresent()) return buildUser(ven.get());

        // 3. Cerca Animatore
        Optional<Animatore> anim = animatoreRepo.findByEmail(email);
        if (anim.isPresent()) return buildUser(anim.get());

        // 4. Cerca Curatore
        Optional<Curatore> cur = curatoreRepo.findByEmail(email);
        if (cur.isPresent()) return buildUser(cur.get());

        throw new UsernameNotFoundException("Utente non trovato con email: " + email);
    }

    // Metodo helper per convertire il nostro AbstractUtente in uno User di Spring Security
    private UserDetails buildUser(AbstractUtente utente) {
        return User.builder()
                .username(utente.getEmail())   // Usiamo l'email come username
                .password(utente.getPassword()) // La password cifrata dal DB
                .authorities(utente.getRuolo()) // Il ruolo (es. ROLE_ACQUIRENTE)
                .disabled(!utente.isApprovato()) // <-- MAGIA: Se approvato=false, il login fallisce qui!
                .build();
    }
}