package bg.softuni.serviceScheduler.vehicle.dao;

import bg.softuni.serviceScheduler.vehicle.model.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, UUID> {

    Optional<OilChange> findFirstByEngineIdOrderByMileageDesc(UUID carId);
}
