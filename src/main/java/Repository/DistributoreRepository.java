package Repository;


import Model.Distributore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DistributoreRepository extends JpaRepository<Distributore, Long> {
    Optional<Distributore> findByNome(String nome);
}
