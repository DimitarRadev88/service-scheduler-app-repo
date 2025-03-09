package bg.softuni.serviceScheduler.insurance.dao;

import bg.softuni.serviceScheduler.insurance.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {

    Boolean existsByIsValidTrueAndCarId(UUID carId);

    @Query(
            """
                    SELECT SUM(i.cost)
                    FROM Insurance i
                    JOIN i.car c
                    JOIN c.user u
                    WHERE u.id = :userId
                    GROUP BY u
                    """
    )
    BigDecimal getSumInsuranceCostByUserId(UUID userId);

}
