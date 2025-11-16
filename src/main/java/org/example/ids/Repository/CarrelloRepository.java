package org.example.ids.Repository;


import org.example.ids.Model.Carrello;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CarrelloRepository extends JpaRepository<Carrello, Long> {
    Optional<Carrello> findByAcquirenteId(Long acquirenteId);


    @Query("""
       select distinct c from Carrello c
       left join fetch c.items i
       left join fetch i.prodotto p
       left join fetch c.bundleItems bi
       left join fetch bi.bundle b
       where c.acquirente.id = :acquirenteId
    """)
    Optional<Carrello> findGraphByAcquirenteId(@Param("acquirenteId") Long acquirenteId);
}
