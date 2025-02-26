package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.validation.Valid;

import java.util.UUID;

public interface UserService {
    UUID doLogin(UserLoginBindingModel userLogin);

    String doRegister(UserRegisterBindingModel userRegister);
}
