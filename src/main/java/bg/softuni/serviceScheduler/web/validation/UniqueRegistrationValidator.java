package bg.softuni.serviceScheduler.web.validation;

import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueRegistrationValidator implements ConstraintValidator<UniqueRegistration, CarAddBindingModel> {

    private final CarRepository carRepository;

    @Autowired
    public UniqueRegistrationValidator(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    @Override
    public boolean isValid(CarAddBindingModel value, ConstraintValidatorContext context) {
        return !carRepository.existsByRegistration(value.registration());
    }
}
