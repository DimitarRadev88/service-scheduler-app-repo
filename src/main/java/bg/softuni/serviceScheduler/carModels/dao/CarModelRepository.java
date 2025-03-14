package bg.softuni.serviceScheduler.carModels.dao;

import bg.softuni.serviceScheduler.carModels.model.CarModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, UUID> {

    Optional<CarModel> findCarModelByBrandNameAndModelName(String make, String model);
}
