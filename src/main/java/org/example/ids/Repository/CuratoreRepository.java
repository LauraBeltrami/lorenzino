package org.example.ids.Repository;


import org.example.ids.Model.Curatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuratoreRepository extends JpaRepository<Curatore, Long> {
    List<Curatore> findByApprovatoFalse();

    Optional<Curatore> findByEmail(String email);
}

