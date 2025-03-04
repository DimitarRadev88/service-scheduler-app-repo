package bg.softuni.serviceScheduler.user.service.impl;

import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.CarInsuranceAddSelectView;
import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInsuranceAddServiceView;
import bg.softuni.serviceScheduler.vehicle.model.OilChange;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String BASIC_PROFILE_PICTURE_URL = "src/main/resources/static/img/avatars/basic-profile-pic.jpg";
    private final CarService carService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CarService carService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.carService = carService;
    }

    @Override
    public UUID doLogin(UserLoginBindingModel userLogin) {
        Optional<User> optionalUser = userRepository.findByEmail(userLogin.email());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(userLogin.password(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        return user.getId();
    }

    @Override
    public String doRegister(UserRegisterBindingModel userRegister) {
        if (userRepository.existsByEmail(userRegister.email())) {
            throw new RuntimeException("Email already exists!");
        }

        if (!userRegister.password().equals(userRegister.confirmPassword())) {
            throw new RuntimeException("Passwords do not match!");
        }

        User user = new User();
        user.setFirstName(userRegister.firstName());
        user.setLastName(userRegister.lastName());
        user.setEmail(userRegister.email());
        user.setPassword(passwordEncoder.encode(userRegister.password()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setProfilePictureURL(BASIC_PROFILE_PICTURE_URL);

        User save = userRepository.save(user);

        log.info("New user: {} saved in database", save);

        return save.getEmail();
    }

    @Override
    @Transactional
    public UserDashboardServiceModelView getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        List<CarDashboardViewServiceModel> cars = user.getCars().stream().map(car -> new CarDashboardViewServiceModel(car.getId(),
                car.getModel().getBrand().getName(),
                car.getModel().getName(),
                car.getVin(),
                car
                        .getEngine()
                        .getOilChanges()
                        .stream()
                        .map(OilChange::getCost)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
                                .add(car.getInsurances().stream().map(Insurance::getCost).reduce(BigDecimal::add).orElse(BigDecimal.ZERO)),
                car
//                        todo
                        .getEngine().getMileage() > 0
        )).toList();

        List<CarDashboardServicesDoneViewServiceModel> services = carService.getAllServices();

        return new UserDashboardServiceModelView(services.size(), user.getRegistrationDate().toLocalDate(), cars, services);
    }

    @Override
    @Transactional
    public UserWithCarsInsuranceAddServiceView getUserWithCarsInsuranceAddServiceView(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserWithCarsInsuranceAddServiceView(
                user.getId(),
                user.getCars().stream().map(car -> new CarInsuranceAddSelectView(
                        car.getId(),
                        car.getModel().getBrand().getName() + " " + car.getModel().getName()
                )).toList()
        );
    }
}
