package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.service.dto.*;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UUID doLogin(UserLoginBindingModel userLogin);

    String doRegister(UserRegisterBindingModel userRegister) throws UsernameAlreadyExistsException, EmailAlreadyExistsException;

    UserDashboardServiceModelView getUser(UUID id);

    UserWithCarsInfoAddServiceView getUserWithCarsInfoAddServiceView(UUID id);

    SiteStatisticsServiceModelView getStatistics();

    List<AllUsersServiceModelView> getAllUsers();

    void removeAdmin(UUID id);

    void makeAdmin(UUID id);

    UserProfileViewServiceModel getUserProfileView(UUID id);

    UserEditProfileServiceModel getUserEditProfileServiceModel(UUID id);

    void doEdit(@Valid UserProfileEditBindingModel userProfileEditBindingModel, UUID id);
}
