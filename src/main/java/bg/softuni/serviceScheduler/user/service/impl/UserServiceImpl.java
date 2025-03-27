package bg.softuni.serviceScheduler.user.service.impl;

import bg.softuni.serviceScheduler.services.oilChange.service.OilChangeService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.user.exception.UserRoleNotFoundException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.*;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String BASIC_PROFILE_PICTURE_URL = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CarService carService;
    private final UserRoleRepository userRoleRepository;
    private final OilChangeService oilChangeService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CarService carService, UserRoleRepository userRoleRepository, OilChangeService oilChangeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.carService = carService;
        this.userRoleRepository = userRoleRepository;
        this.oilChangeService = oilChangeService;
    }

    @Override
    public String doRegister(UserRegisterBindingModel userRegister) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        if (userRepository.existsByUsername(userRegister.username())) {
            throw new UsernameAlreadyExistsException("Username already exists!");
        }

        if (userRepository.existsByEmail(userRegister.email())) {
            throw new EmailAlreadyExistsException("Email already exists!");
        }

        User user = new User(
                null,
                userRegister.username(),
                passwordEncoder.encode(userRegister.password()),
                userRegister.email(),
                LocalDateTime.now(),
                BASIC_PROFILE_PICTURE_URL,
                new ArrayList<>(),
                new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)))
        );

        if (userRepository.count() == 0) {
            user.getRoles().add(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN));
        }

        User save = userRepository.save(user);

        log.info("New user: {} saved in database", save);

        return save.getUsername();
    }

    @Override
    public UserDashboardServiceModelView getUser(UUID id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<CarDashboardViewServiceModel> cars = carService.getAllCarDashboardServiceViewModelsByUser(user.getId());
        List<CarDashboardServicesDoneViewServiceModel> services = carService.getAllServicesByUser(user.getId());

        return new UserDashboardServiceModelView(user.getRegistrationDate().toLocalDate(), cars, services);
    }

    @Override
    public UserWithCarsInfoAddServiceView getUserWithCarsInfoAddServiceView(UUID id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserWithCarsInfoAddServiceView(
                user.getId(),
                carService.getCarInsuranceAddSelectView(user.getId())
        );
    }

    @Override
    public SiteStatisticsServiceModelView getStatistics() {
        return new SiteStatisticsServiceModelView(userRepository.count(), oilChangeService.getOilChangesCount());

    }

    @Transactional
    @Override
    public List<AllUsersServiceModelView> getAllUsers() {
        return userRepository.findAllByOrderByRegistrationDateAsc().stream().map(user -> new AllUsersServiceModelView(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream().anyMatch(userRole -> userRole.getRole().equals(UserRoleEnumeration.ADMIN))
                        ? UserRoleEnumeration.ADMIN
                        : UserRoleEnumeration.USER
        )).toList();
    }

    @Override
    @Transactional
    public void removeAdmin(UUID id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserRole userRole = user
                .getRoles()
                .stream()
                .filter(role -> role.getRole().equals(UserRoleEnumeration.ADMIN))
                .findFirst()
                .orElseThrow(() -> new UserRoleNotFoundException("Role not found"));

        user.getRoles().remove(userRole);

        userRepository.save(user);

        log.info("Removed admin role for user: {}", user.getUsername());
    }

    @Transactional
    @Override
    public void makeAdmin(UUID id) {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user
                .getRoles()
                .add(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN));

        userRepository.save(user);

        log.info("User {} promoted to admin", user.getUsername());
    }

    @Override
    public UserProfileViewServiceModel getUserProfileView(UUID id) {
        return userRepository
                .findById(id)
                .map(u -> new UserProfileViewServiceModel(
                        u.getUsername(),
                        u.getEmail(),
                        u.getRegistrationDate().toLocalDate(),
                        u.getProfilePictureURL()
                ))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public UserEditProfileServiceModel getUserEditProfileServiceModel(UUID id) {
        return userRepository
                .findById(id)
                .map(u -> new UserEditProfileServiceModel(
                        u.getUsername(),
                        u.getEmail(),
                        u.getProfilePictureURL()
                ))
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    @Override
    public void doEdit(UserProfileEditBindingModel userProfileEditBindingModel, UUID id) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getUsername().equals(userProfileEditBindingModel.username()) && userRepository.existsByUsername(userProfileEditBindingModel.username())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        if (!user.getEmail().equals(userProfileEditBindingModel.email()) && userRepository.existsByEmail(userProfileEditBindingModel.email())) {
            throw new EmailAlreadyExistsException("Email already exists");
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
