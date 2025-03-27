package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.carServices.oilChange.service.OilChangeService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.exception.EmailAlreadyExistsException;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.user.exception.UserRoleNotFoundException;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.dto.*;
import bg.softuni.serviceScheduler.user.service.impl.UserServiceImpl;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.naming.AuthenticationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    private static final String BASIC_PROFILE_PICTURE_URL = "https://t4.ftcdn.net/jpg/04/10/43/77/360_F_410437733_hdq4Q3QOH9uwh0mcqAhRFzOKfrCR24Ta.jpg";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_USERNAME = "Username";
    private static final String USER_PASSWORD = "Password";
    private static final String USER_EMAIL = "Email";
    private static final LocalDateTime USER_REGISTRATION_DATE = LocalDateTime.now();
    private static final String USER_PROFILE_PICTURE_URL = "Profile Picture URL";
    private static final List<Car> USER_CARS = new ArrayList<>(List.of(new Car()));
    private static final UserRole USER_ROLE_USER = new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER);
    private static final UserRole USER_ROLE_ADMIN = new UserRole(UUID.randomUUID(), UserRoleEnumeration.ADMIN);
    private static final String NEW_EMAIL = "New Email";
    private static final String NEW_USERNAME = "New Username";
    private static final String NEW_URL = "new url";
    private static final String BLANK_URL = " ";
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;
    @Mock
    private CarService carService;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private OilChangeService oilChangeService;

    private UserService userService;
    private User user;
    @Captor
    private ArgumentCaptor<User> userCaptor;

    @BeforeEach
    void setUp() {
        this.userCaptor = ArgumentCaptor.forClass(User.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder, carService, userRoleRepository, oilChangeService);
        user = new User(USER_ID,
                USER_USERNAME,
                USER_PASSWORD,
                USER_EMAIL,
                USER_REGISTRATION_DATE,
                USER_PROFILE_PICTURE_URL,
                USER_CARS,
                new ArrayList<>(List.of(USER_ROLE_USER, USER_ROLE_ADMIN))
        );
    }

    @Test
    public void testDoEditSetsBasicProfilePictureUrlWhenUrlIsBlank() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                USER_USERNAME, USER_EMAIL, BLANK_URL
        );

        userService.doEdit(editBindingModel, USER_ID);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(BASIC_PROFILE_PICTURE_URL, saved.getProfilePictureURL());
    }

    @Test
    public void testDoEditChangesProfilePictureUrl() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                USER_USERNAME, USER_EMAIL, NEW_URL
        );

        userService.doEdit(editBindingModel, USER_ID);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(NEW_URL, saved.getProfilePictureURL());
    }

    @Test
    public void testDoEditChangesEmail() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                USER_USERNAME, NEW_EMAIL, USER_PROFILE_PICTURE_URL
        );

        userService.doEdit(editBindingModel, USER_ID);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(NEW_EMAIL, saved.getEmail());
    }

    @Test
    public void testDoEditChangesUsername() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                NEW_USERNAME, USER_EMAIL, USER_PROFILE_PICTURE_URL
        );

        userService.doEdit(editBindingModel, USER_ID);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(NEW_USERNAME, saved.getUsername());
    }

    @Test
    public void testDoEditThrowsWhenEmailIsChangedAndNewEmailExists() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userRepository.existsByEmail(NEW_EMAIL))
                .thenReturn(true);

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                USER_USERNAME, NEW_EMAIL, USER_PROFILE_PICTURE_URL
        );

        assertThrows(EmailAlreadyExistsException.class, () -> userService.doEdit(editBindingModel, USER_ID));
    }

    @Test
    public void testDoEditThrowsWhenUsernameIsChangedAndNewUsernameExists() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        Mockito
                .when(userRepository.existsByUsername(NEW_USERNAME))
                .thenReturn(true);

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                NEW_USERNAME, USER_EMAIL, USER_PROFILE_PICTURE_URL
        );

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.doEdit(editBindingModel, USER_ID));
    }

    @Test
    public void testDoEditThrowsWhenUserDoesNotExist() throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        UserProfileEditBindingModel editBindingModel = new UserProfileEditBindingModel(
                USER_USERNAME, USER_EMAIL, USER_PROFILE_PICTURE_URL
        );

        assertThrows(UserNotFoundException.class, () -> userService.doEdit(editBindingModel, USER_ID));
    }

    @Test
    public void testGetUserEditProfileServiceModelReturnsCorrectModelValues() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserEditProfileServiceModel expected = new UserEditProfileServiceModel(
                USER_USERNAME, USER_EMAIL, USER_PROFILE_PICTURE_URL
        );

        UserEditProfileServiceModel actual = userService.getUserEditProfileServiceModel(USER_ID);

        assertEquals(expected, actual);
    }


    @Test
    public void testGetUserEditProfileServiceModelThrowsWhenUserDoesNotExist() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserEditProfileServiceModel(USER_ID));
    }

    @Test
    public void testGetUserProfileViewReturnsCorrectModelValues() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        UserProfileViewServiceModel expected = new UserProfileViewServiceModel(
                USER_USERNAME, USER_EMAIL, USER_REGISTRATION_DATE.toLocalDate(), USER_PROFILE_PICTURE_URL
        );

        UserProfileViewServiceModel actual = userService.getUserProfileView(USER_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetUserProfileViewThrowsWhenUserDoesNotExist() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserProfileView(USER_ID));
    }

    @Test
    public void testMakeAdminAddsAdminRoleToUserRoles() {
        user.setRoles(new ArrayList<>(List.of(USER_ROLE_USER)));
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        Mockito
                .when(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN))
                .thenReturn(USER_ROLE_ADMIN);

        userService.makeAdmin(USER_ID);

        verify(userRepository).save(userCaptor.capture());

        userCaptor.getValue();

        assertTrue(user.getRoles().contains(USER_ROLE_ADMIN));
    }

    @Test
    public void testMakeAdminThrowsWhenUserDoesNotExist() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.makeAdmin(USER_ID));
    }

    @Test
    public void testRemoveAdminRemovesAdminRoleFromUserRoles() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        userService.removeAdmin(USER_ID);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertFalse(saved.getRoles().contains(USER_ROLE_ADMIN));
    }

    @Test
    public void testRemoveAdminThrowsWhenUserNotAdmin() {
        user.getRoles().remove(USER_ROLE_ADMIN);

        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));

        assertThrows(UserRoleNotFoundException.class, () -> userService.removeAdmin(USER_ID));
    }

    @Test
    public void testRemoveAdminThrowsWhenUserDoesNotExist() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.removeAdmin(USER_ID));
    }

    @Test
    public void testGetAllUsersReturnsCorrectModels() {
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();
        UUID thirdUserId = UUID.randomUUID();
        String firstUsername = "first user";
        String secondUsername = "second user";
        String thirdUsername = "third user";

        User first = new User();
        first.setId(firstUserId);
        first.setUsername(firstUsername);
        first.setRoles(List.of(USER_ROLE_USER, USER_ROLE_ADMIN));

        User second = new User();
        second.setId(secondUserId);
        second.setUsername(secondUsername);
        second.setRoles(List.of(USER_ROLE_USER));

        User third = new User();
        third.setId(thirdUserId);
        third.setUsername(thirdUsername);
        third.setRoles(List.of(USER_ROLE_USER));

        List<User> users = List.of(first, second, third);
        Mockito
                .when(userRepository.findAllByOrderByRegistrationDateAsc())
                .thenReturn(users);

        List<AllUsersServiceModelView> expected = users.stream().map(user -> new AllUsersServiceModelView(
                        user.getId(),
                        user.getUsername(),
                        user.getRoles().contains(USER_ROLE_ADMIN) ? UserRoleEnumeration.ADMIN : UserRoleEnumeration.USER
                )
        ).toList();

        List<AllUsersServiceModelView> actual = userService.getAllUsers();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetStatisticsReturnsCorrectResult() {
        Mockito.when(userRepository.count()).thenReturn(1L);
        Mockito.when(oilChangeService.getOilChangesCount()).thenReturn(1L);

        SiteStatisticsServiceModelView statistics = userService.getStatistics();

        assertEquals(1L, statistics.registeredUsers());
        assertEquals(1L, statistics.oilChanges());

        Mockito.when(userRepository.count()).thenReturn(0L);
        Mockito.when(oilChangeService.getOilChangesCount()).thenReturn(0L);

        statistics = userService.getStatistics();

        assertEquals(0L, statistics.registeredUsers());
        assertEquals(0L, statistics.oilChanges());

        Mockito.when(userRepository.count()).thenReturn(356734567L);
        Mockito.when(oilChangeService.getOilChangesCount()).thenReturn(4675834675L);

        statistics = userService.getStatistics();

        assertEquals(356734567L, statistics.registeredUsers());
        assertEquals(4675834675L, statistics.oilChanges());
    }

    @Test
    public void testGetUserWithCarsInfoAddServiceViewReturnsCorrectModelValues() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        Mockito
                .when(carService.getCarInsuranceAddSelectView(USER_ID))
                .thenReturn(new ArrayList<>());

        UserWithCarsInfoAddServiceView actual = userService.getUserWithCarsInfoAddServiceView(USER_ID);

        assertEquals(USER_ID, actual.id());
        assertTrue(actual.cars().isEmpty());
    }

    @Test
    public void testGetUserWithCarsInfoAddServiceViewThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserWithCarsInfoAddServiceView(USER_ID));
    }

    @Test
    public void testGetUserReturnsCorrectModelValues() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(user));
        Mockito
                .when(carService.getAllCarDashboardServiceViewModelsByUser(USER_ID))
                .thenReturn(new ArrayList<>());
        Mockito
                .when(carService.getAllServicesByUser(USER_ID))
                .thenReturn(new ArrayList<>());


        UserDashboardServiceModelView actual = userService.getUser(USER_ID);

        assertEquals(USER_REGISTRATION_DATE.toLocalDate(), actual.registrationDate());
        assertTrue(actual.services().isEmpty());
        assertTrue(actual.cars().isEmpty());
    }

    @Test
    public void testGetUserThrowsWhenUserIdDoesNotExist() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUser(USER_ID));
    }

    @Test
    public void testDoRegisterSavesUserAsAdminWhenRepositoryIsEmpty() throws AuthenticationException {
        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel(
                USER_USERNAME, USER_EMAIL, USER_PASSWORD, USER_PASSWORD
        );

        User user = new User();
        user.setUsername(USER_USERNAME);

        Mockito
                .when(userRepository.existsByUsername(USER_USERNAME))
                .thenReturn(false);
        Mockito
                .when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(false);
        Mockito
                .when(userRoleRepository.findByRole(UserRoleEnumeration.USER))
                .thenReturn(USER_ROLE_USER);
        Mockito
                .when(userRoleRepository.findByRole(UserRoleEnumeration.ADMIN))
                .thenReturn(USER_ROLE_ADMIN);

        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        Mockito
                .when(userRepository.count())
                .thenReturn(0L);

        String actual = userService.doRegister(userRegisterBindingModel);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(USER_USERNAME, actual);
        assertEquals(USER_USERNAME, saved.getUsername());
        assertEquals(USER_EMAIL, saved.getEmail());
        assertTrue(passwordEncoder.matches(USER_PASSWORD, saved.getPassword()));
        assertEquals(LocalDate.now(), saved.getRegistrationDate().toLocalDate());
        assertEquals(BASIC_PROFILE_PICTURE_URL, saved.getProfilePictureURL());
        assertTrue(saved.getCars().isEmpty());
        assertEquals(new ArrayList<>(List.of(USER_ROLE_USER, USER_ROLE_ADMIN)), saved.getRoles());

    }

    @Test
    public void testDoRegisterSavesUser() throws AuthenticationException {
        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel(
                USER_USERNAME, USER_EMAIL, USER_PASSWORD, USER_PASSWORD
        );

        User user = new User();
        user.setUsername(USER_USERNAME);

        Mockito
                .when(userRepository.existsByUsername(USER_USERNAME))
                .thenReturn(false);
        Mockito
                .when(userRepository.existsByEmail(USER_EMAIL))
                .thenReturn(false);
        Mockito
                .when(userRoleRepository.findByRole(UserRoleEnumeration.USER))
                .thenReturn(USER_ROLE_USER);

        Mockito
                .when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(user);

        Mockito
                .when(userRepository.count())
                .thenReturn(1L);

        String actual = userService.doRegister(userRegisterBindingModel);

        verify(userRepository).save(userCaptor.capture());

        User saved = userCaptor.getValue();

        assertEquals(USER_USERNAME, actual);
        assertEquals(USER_USERNAME, saved.getUsername());
        assertEquals(USER_EMAIL, saved.getEmail());
        assertTrue(passwordEncoder.matches(USER_PASSWORD, saved.getPassword()));
        assertEquals(LocalDate.now(), saved.getRegistrationDate().toLocalDate());
        assertEquals(BASIC_PROFILE_PICTURE_URL, saved.getProfilePictureURL());
        assertTrue(saved.getCars().isEmpty());
        assertEquals(new ArrayList<>(List.of(USER_ROLE_USER)), saved.getRoles());

    }

    @Test
    public void testDoRegisterThrowsWhenEmailExists() {
        Mockito
                .when(userRepository.existsByEmail(Mockito.anyString()))
                .thenReturn(true);

        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel(USER_USERNAME, USER_EMAIL, USER_PASSWORD, USER_PASSWORD);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.doRegister(userRegisterBindingModel));
    }

    @Test
    public void testDoRegisterThrowsWhenUsernameExists() {
        Mockito
                .when(userRepository.existsByUsername(Mockito.anyString()))
                .thenReturn(true);

        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel(USER_USERNAME, USER_EMAIL, USER_PASSWORD, USER_PASSWORD);

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.doRegister(userRegisterBindingModel));
    }

}
