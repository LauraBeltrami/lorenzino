package Repository;


import Model.BundleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BundleItemRepository extends JpaRepository<BundleItem, Long> {
    Optional<BundleItem> findByBundleIdAndProdottoId(Long bundleId, Long prodottoId);
}

