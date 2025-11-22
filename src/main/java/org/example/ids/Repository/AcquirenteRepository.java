package org.example.ids.Repository;


import org.example.ids.Model.Acquirente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcquirenteRepository extends JpaRepository<Acquirente, Long> {
    Optional<Acquirente> findByEmail(String email);
}


