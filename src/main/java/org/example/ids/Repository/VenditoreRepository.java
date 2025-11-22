package org.example.ids.Repository;


import org.example.ids.Model.Venditore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenditoreRepository extends JpaRepository<Venditore, Long> {
    List<Venditore> findByApprovatoFalse();
}

