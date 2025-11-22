package org.example.ids.Repository;


import org.example.ids.Model.Animatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnimatoreRepository extends JpaRepository<Animatore, Long> {
    List<Animatore> findByApprovatoFalse();
}


