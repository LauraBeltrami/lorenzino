package Repository;


import Model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    @Query("""
       select distinct e from Evento e
       left join fetch e.inviti i
       left join fetch i.venditore v
       where e.id = :id
    """)
    Optional<Evento> findGraphById(@Param("id") Long id);

    @Query("""
       select distinct e from Evento e
       left join fetch e.inviti i
       left join fetch i.venditore v
       where e.animatore.id = :animatoreId
    """)
    List<Evento> findGraphByAnimatoreId(@Param("animatoreId") Long animatoreId);
}