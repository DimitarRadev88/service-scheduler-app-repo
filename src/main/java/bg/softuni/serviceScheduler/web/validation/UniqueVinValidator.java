package bg.softuni.serviceScheduler.web.validation;

import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class UniqueVinValidator implements ConstraintValidator<UniqueVin, CarAddBindingModel> {

    private final CarRepository carRepository;

    public UniqueVinValidator(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public boolean isValid(CarAddBindingModel value, ConstraintValidatorContext context) {
        return !carRepository.existsByVin(value.vin());
    }

}
