package bg.softuni.serviceScheduler.vignette.dao;

import bg.softuni.serviceScheduler.vignette.model.Vignette;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface VignetteRepository extends JpaRepository<Vignette, UUID> {

    boolean existsByCarIdAndIsValidTrue(UUID id);

    @Query("""
            SELECT SUM(v.cost)
                    FROM Vignette v
                    JOIN v.car c
                    JOIN c.user u
                    WHERE u.id = :userId
                    GROUP BY u
            """)
    BigDecimal getSumVignetteCostByUserId(UUID userId);
}
