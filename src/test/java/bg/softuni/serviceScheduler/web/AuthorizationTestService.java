package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.impl.ServiceSchedulerUserDetailsService;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AuthorizationTestService {


    static final UUID USER_ID = UUID.fromString("6a287caf-6962-4eca-a1c5-2a201734a0cb");
    private UserService userService = Mockito.mock(UserService.class);
    private UserRepository userRepository = Mockito.mock(UserRepository.class);

    private ServiceSchedulerUserDetailsService serviceSchedulerUserDetailsService;

    public AuthorizationTestService() {
        this.serviceSchedulerUserDetailsService = new ServiceSchedulerUserDetailsService(userRepository);
    }

    public UserDetails getUserDetailsUser() {
        User user = getUser();

        Mockito.when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        return serviceSchedulerUserDetailsService.loadUserByUsername(user.getUsername());
    }

    public User getUser() {
        return new User(
                USER_ID,
                "user",
                "password",
                "email@email",
                LocalDateTime.now(),
                "profile picture",
                new ArrayList<>(),
                List.of(new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER))
        );

    }

    public User getUserAdmin() {
        User user = getUser();
        user.setRoles(List.of(
                new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER),
                new UserRole(UUID.randomUUID(), UserRoleEnumeration.ADMIN)
        ));
        return user;
    }

    public UserDetails getUserDetailsAdmin() {
        User user = getUserAdmin();

        Mockito.when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        return serviceSchedulerUserDetailsService.loadUserByUsername(user.getUsername());
    }
}
