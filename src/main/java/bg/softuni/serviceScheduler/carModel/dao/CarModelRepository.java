package bg.softuni.serviceScheduler.carModel.dao;

import bg.softuni.serviceScheduler.carModel.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, UUID> {

    Optional<CarModel> findCarModelByBrandNameAndModelName(String make, String model);
}
