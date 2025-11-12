package Repository;


import Model.CarrelloItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrelloItemRepository extends JpaRepository<CarrelloItem, Long> {
    Optional<CarrelloItem> findByCarrelloIdAndProdottoId(Long carrelloId, Long prodottoId);
}