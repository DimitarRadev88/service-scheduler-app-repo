package bg.softuni.serviceScheduler.vignette.dao;

import bg.softuni.serviceScheduler.vignette.model.Vignette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VignetteRepository extends JpaRepository<Vignette, UUID> {

    boolean existsByCarIdAndIsValidTrue(UUID id);
}
