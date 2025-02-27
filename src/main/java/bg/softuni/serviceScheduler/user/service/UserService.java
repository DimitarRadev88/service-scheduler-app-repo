package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;

import java.util.UUID;

public interface UserService {
    UUID doLogin(UserLoginBindingModel userLogin);

    String doRegister(UserRegisterBindingModel userRegister);

    UserDashboardServiceModelView getUser(UUID id);
}
