package bg.softuni.serviceScheduler.web.validation;

import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsValueMatchValidator implements ConstraintValidator<PasswordsMatch, UserRegisterBindingModel> {

    @Override
    public boolean isValid(UserRegisterBindingModel userRegister, ConstraintValidatorContext context) {
        return userRegister.password().equals(userRegister.confirmPassword());
    }

}
