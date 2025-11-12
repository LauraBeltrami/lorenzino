package Repository;

import Model.Prodotto;
import Model.StatoProdotto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdottoRepository extends JpaRepository<Prodotto, Long> {
    List<Prodotto> findByStato(StatoProdotto stato);
}
