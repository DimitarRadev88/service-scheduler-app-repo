package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handeUsernameAlreadyExists(Exception ex, Model model) {
        model.addAttribute("usernameException", ex.getMessage());
        model.addAttribute("userRegister", new UserRegisterBindingModel());
        return "register";
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handeEmailAlreadyExists(Exception ex, Model model) {
        model.addAttribute("emailException", ex.getMessage());
        model.addAttribute("userRegister", new UserRegisterBindingModel());
        return "register";
    }

}
