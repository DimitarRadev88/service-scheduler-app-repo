package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.user.service.dto.AllUsersServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UUID doLogin(UserLoginBindingModel userLogin);

    String doRegister(UserRegisterBindingModel userRegister);

    UserDashboardServiceModelView getUser(UUID id);

    UserWithCarsInfoAddServiceView getUserWithCarsInfoAddServiceView(UUID id);

    SiteStatisticsServiceModelView getStatistics();

    List<AllUsersServiceModelView> getAllUsers();

    void removeAdmin(UUID id);

    void makeAdmin(UUID id);
}
