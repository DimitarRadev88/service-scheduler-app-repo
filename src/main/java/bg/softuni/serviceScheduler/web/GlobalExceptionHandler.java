package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carModel.exception.CarModelAddException;
import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handeUsernameAlreadyExists(Exception ex, Model model) {
        model.addAttribute("usernameException", ex.getMessage());
        model.addAttribute("userRegister", new UserRegisterBindingModel());
        return "register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handeEmailAlreadyExists(Exception ex, Model model) {
        model.addAttribute("emailException", ex.getMessage());
        model.addAttribute("userRegister", new UserRegisterBindingModel());
        return "register";
    }

    @ExceptionHandler(CarModelAddException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handeCarModelAddException(Exception ex, Model model) {
        model.addAttribute("message", ex.getMessage());

        return "car-model-add-error";
    }

}
