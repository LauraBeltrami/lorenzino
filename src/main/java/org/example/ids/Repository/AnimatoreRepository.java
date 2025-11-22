package org.example.ids.Repository;


import org.example.ids.Model.Animatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnimatoreRepository extends JpaRepository<Animatore, Long> {
    List<Animatore> findByApprovatoFalse();

    Optional<Animatore> findByEmail(String email);
}


