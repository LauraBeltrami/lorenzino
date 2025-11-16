package org.example.ids.Repository;

import org.example.ids.Model.Prodotto;
import org.example.ids.Model.StatoProdotto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {
    List<Prodotto> findByStato(StatoProdotto stato);
}
