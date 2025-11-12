package Repository;


import Model.InvitoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitoEventoRepository extends JpaRepository<InvitoEvento, Long> {
    Optional<InvitoEvento> findByEventoIdAndVenditoreId(Long eventoId, Long venditoreId);
}
