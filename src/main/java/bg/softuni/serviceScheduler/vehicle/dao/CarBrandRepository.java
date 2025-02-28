package bg.softuni.serviceScheduler.vehicle.dao;

import bg.softuni.serviceScheduler.vehicle.model.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, UUID> {

}
