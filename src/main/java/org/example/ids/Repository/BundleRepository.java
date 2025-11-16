package org.example.ids.Repository;


import org.example.ids.Model.Bundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BundleRepository extends JpaRepository<Bundle, Long> {
    List<Bundle> findByDistributoreId(Long distributoreId);

    @Query("""
       select distinct b from Bundle b
       left join fetch b.items bi
       left join fetch bi.prodotto p
       where b.id = :id
    """)
    Optional<Bundle> findGraphById(@Param("id") Long id);

    @Query("""
       select distinct b from Bundle b
       left join fetch b.items bi
       left join fetch bi.prodotto p
       where b.distributore.id = :distributoreId
    """)
    List<Bundle> findGraphByDistributoreId(@Param("distributoreId") Long distributoreId);

    @Query("""
       select distinct b from Bundle b
       left join fetch b.items bi
       left join fetch bi.prodotto p
    """)
    List<Bundle> findAllGraph();
}

