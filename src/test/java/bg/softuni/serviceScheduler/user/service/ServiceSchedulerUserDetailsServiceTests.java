package bg.softuni.serviceScheduler.user.service;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UsernameAlreadyExistsException;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.impl.ServiceSchedulerUserDetailsService;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ServiceSchedulerUserDetailsServiceTests {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_USERNAME = "Username";
    private static final String USER_PASSWORD = "Password";
    private static final String USER_EMAIL = "Email";
    private static final LocalDateTime USER_REGISTRATION_DATE = LocalDateTime.now();
    private static final String USER_PROFILE_PICTURE_URL = "Profile Picture URL";
    private static final List<Car> USER_CARS = new ArrayList<>(List.of(new Car()));
    private static final UserRole USER_ROLE_USER = new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER);
    private static final UserRole USER_ROLE_ADMIN = new UserRole(UUID.randomUUID(), UserRoleEnumeration.ADMIN);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private UserDetailsService userDetailsService;
    private User user;

    @BeforeEach
    void setUp() {
        userDetailsService = new ServiceSchedulerUserDetailsService(userRepository);
        user = new User(USER_ID, USER_USERNAME, USER_PASSWORD, USER_EMAIL, USER_REGISTRATION_DATE,
                USER_PROFILE_PICTURE_URL, USER_CARS, List.of(USER_ROLE_USER, USER_ROLE_ADMIN));
    }

    @Test
    public void testLoadUserByUsernameReturnsCorrectModelValues() throws UsernameNotFoundException {
        Mockito
                .when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.of(user));

        UserDetails expected = new ServiceSchedulerUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>(List.of(
                        new SimpleGrantedAuthority("ROLE_" + UserRoleEnumeration.USER),
                        new SimpleGrantedAuthority("ROLE_" + UserRoleEnumeration.ADMIN))
                )
        );

        UserDetails actual = userDetailsService.loadUserByUsername(USER_USERNAME);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void testLoadUserByUsernameThrowsWhenUserDoesNotExist() throws UsernameNotFoundException {
        Mockito
                .when(userRepository.findByUsername(Mockito.anyString()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(USER_USERNAME));
    }

}
