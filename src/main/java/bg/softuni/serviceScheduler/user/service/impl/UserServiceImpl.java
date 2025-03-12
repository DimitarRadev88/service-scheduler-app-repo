package bg.softuni.serviceScheduler.user.service.impl;

import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.SiteStatisticsServiceModelView;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.*;
import bg.softuni.serviceScheduler.vehicle.model.OilChange;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.web.dto.UserLoginBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final String BASIC_PROFILE_PICTURE_URL = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
    private final CarService carService;
    private final InsuranceService insuranceService;
    private final VignetteService vignetteService;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CarService carService, InsuranceService insuranceService, VignetteService vignetteService, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.carService = carService;
        this.insuranceService = insuranceService;
        this.vignetteService = vignetteService;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UUID doLogin(UserLoginBindingModel userLogin) {
        Optional<User> optionalUser = userRepository.findByUsername(userLogin.username());

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
    public String doRegister(UserRegisterBindingModel userRegister) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (userRepository.existsByUsername(userRegister.username())) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        if (userRepository.existsByEmail(userRegister.email())) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        User user = new User();
        user.setUsername(userRegister.username());
        user.setEmail(userRegister.email());
        user.setPassword(passwordEncoder.encode(userRegister.password()));
        user.setRegistrationDate(LocalDateTime.now());
        user.setProfilePictureURL(BASIC_PROFILE_PICTURE_URL);
        user.setRoles(new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER))));
        if (userRepository.count() == 0) {
            user.getRoles().add(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN));
        }

        User save = userRepository.save(user);

        log.info("New user: {} saved in database", save);

        return save.getUsername();
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
                !insuranceService.hasActiveInsurance(car.getId())
                || carService.needsOilChange(car.getEngine())
                || !vignetteService.hasActiveVignette(car.getId())
        )).toList();

        return new UserDashboardServiceModelView(user.getRegistrationDate().toLocalDate(), cars, carService.getAllServicesByUser(user.getId()));
    }

    @Override
    @Transactional
    public UserWithCarsInfoAddServiceView getUserWithCarsInfoAddServiceView(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return new UserWithCarsInfoAddServiceView(
                user.getId(),
                user.getCars().stream().map(car -> new CarInsuranceAddSelectView(
                        car.getId(),
                        car.getModel().getBrand().getName() + " " + car.getModel().getName()
                )).toList()
        );
    }

    @Override
    public SiteStatisticsServiceModelView getStatistics() {
        return new SiteStatisticsServiceModelView(userRepository.count(), carService.getOilChangesCount());

    }

    @Transactional
    @Override
    public List<AllUsersServiceModelView> getAllUsers() {
        return userRepository.findAllByOrderByRegistrationDateAsc().stream().map(user -> new AllUsersServiceModelView(
                user.getId(),
                user.getUsername(),
                user.getRoles().size() == 2 ? UserRoleEnumeration.ADMIN : UserRoleEnumeration.USER
        )).toList();
    }

    @Override
    @Transactional
    public void removeAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserRole userRole = user.getRoles()
                .stream()
                .filter(role -> role.getRole().equals(UserRoleEnumeration.ADMIN))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(userRole);
        userRepository.save(user);
        log.info("Removed admin role for user: {}", user.getUsername());
    }

    @Transactional
    @Override
    public void makeAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.getRoles().add(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN));
        userRepository.save(user);
        log.info("User {} promoted to admin", user.getUsername());
    }

    @Override
    public UserProfileViewServiceModel getUserProfileView(UUID id) {
        return userRepository.findById(id).map(u -> new UserProfileViewServiceModel(
                u.getUsername(),
                u.getEmail(),
                u.getRegistrationDate().toLocalDate(),
                u.getProfilePictureURL()
        )).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @Override
    public UserEditProfileServiceModel getUserEditProfileServiceModel(UUID id) {
        return userRepository.findById(id).map(u -> new UserEditProfileServiceModel(
                u.getUsername(),
                u.getEmail(),
                u.getProfilePictureURL()
        )).orElseThrow(() -> new RuntimeException("User not found!"));
    }

    @Override
    public void doEdit(UserProfileEditBindingModel userProfileEditBindingModel, UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getUsername().equals(userProfileEditBindingModel.username()) && userRepository.existsByUsername(userProfileEditBindingModel.username())) {
            throw new RuntimeException("Username already exists");
        }

        if (!user.getEmail().equals(userProfileEditBindingModel.email()) && userRepository.existsByEmail(userProfileEditBindingModel.email())) {
            throw new RuntimeException("Email already exists");
        }

        user.setUsername(userProfileEditBindingModel.username());
        user.setEmail(userProfileEditBindingModel.email());
        user.setProfilePictureURL(userProfileEditBindingModel.profilePictureUrl());
        if (user.getProfilePictureURL().isBlank()) {
            user.setProfilePictureURL(BASIC_PROFILE_PICTURE_URL);
        }

        userRepository.save(user);
        log.info("User {} profile edited", user.getUsername());
    }
}
