package org.example.ids.Repository;


import org.example.ids.Model.CarrelloBundleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarrelloBundleItemRepository extends JpaRepository<CarrelloBundleItem, Long> {
    Optional<CarrelloBundleItem> findByCarrelloIdAndBundleId(Long carrelloId, Long bundleId);
}
