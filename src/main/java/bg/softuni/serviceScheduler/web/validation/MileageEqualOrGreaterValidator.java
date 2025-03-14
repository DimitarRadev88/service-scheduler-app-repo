package bg.softuni.serviceScheduler.web.validation;

import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MileageEqualOrGreaterValidator implements ConstraintValidator<MileageEqualOrGreater, EngineMileageAddBindingModel> {


    @Override
    public boolean isValid(EngineMileageAddBindingModel mileageAdd, ConstraintValidatorContext context) {
        return mileageAdd.newMileage() >= mileageAdd.oldMileage();
    }
}
