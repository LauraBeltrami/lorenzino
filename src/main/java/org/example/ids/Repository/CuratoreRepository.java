package org.example.ids.Repository;


import org.example.ids.Model.Curatore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CuratoreRepository extends JpaRepository<Curatore, Long> {
    List<Curatore> findByApprovatoFalse();
}

