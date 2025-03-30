package bg.softuni.serviceScheduler.web.validation;

import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MileageEqualOrGreaterValidator implements ConstraintValidator<MileageEqualOrGreater, EngineMileageAddBindingModel> {


    @Override
    public boolean isValid(EngineMileageAddBindingModel mileageAdd, ConstraintValidatorContext context) {
        if (mileageAdd.oldMileage() == null || mileageAdd.newMileage() == null) {
            return false;
        }
        return mileageAdd.newMileage() >= mileageAdd.oldMileage();
    }
}
