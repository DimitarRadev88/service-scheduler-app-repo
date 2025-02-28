package bg.softuni.serviceScheduler.vehicle.dao;

import bg.softuni.serviceScheduler.vehicle.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, UUID> {
}
