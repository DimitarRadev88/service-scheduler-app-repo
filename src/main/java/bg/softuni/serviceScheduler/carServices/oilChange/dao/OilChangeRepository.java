package bg.softuni.serviceScheduler.carServices.oilChange.dao;

import bg.softuni.serviceScheduler.carServices.oilChange.model.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, UUID> {

    Optional<OilChange> findFirstByEngineIdOrderByMileageDesc(UUID carId);

    @Query("""
            SELECT SUM(oc.cost)
            FROM OilChange oc
            JOIN oc.engine e
            JOIN e.car c
            JOIN c.user u
            WHERE u.id = :userId
            GROUP BY u
            """)
    BigDecimal getSumOilChangesCostByUserId(UUID userId);

    @Query("""
            SELECT SUM(oc.cost)
            FROM OilChange oc
            JOIN oc.engine e
            WHERE e.id = :engineId
            GROUP BY e.id
            """)
    BigDecimal getSumOilChangesCostByEngineId(UUID engineId);

}
