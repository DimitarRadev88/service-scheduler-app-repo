package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.SiteStatisticsServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import bg.softuni.serviceScheduler.user.service.impl.ServiceSchedulerUserDetailsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerApiTest {

    @MockitoBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    ServiceSchedulerUserDetailsService serviceSchedulerUserDetailsService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    public void testViewHomeReturnsIndexWhenUserNotLoggedIn() throws Exception {
        SiteStatisticsServiceModelView statistics = new SiteStatisticsServiceModelView(1L, 1L);
        Mockito.when(this.userService.getStatistics()).
                thenReturn(statistics);

        mockMvc
                .perform(get("/").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("statistics", statistics));
    }

    @Test
    public void testViewHomeReturnsHomeWhenUserLoggedIn() throws Exception {

        serviceSchedulerUserDetailsService = new ServiceSchedulerUserDetailsService(userRepository);

        User user = new User(
                UUID.randomUUID(),
                "user",
                "password",
                "email",
                LocalDateTime.now(),
                "profile picture",
                new ArrayList<>(),
                List.of(new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER))
        );
        Mockito.when(userRepository.findByUsername("user"))
                        .thenReturn(Optional.of(user));

        UserDetails userDetails = serviceSchedulerUserDetailsService.loadUserByUsername("user");

        Mockito.when(userService
                .getUser(((ServiceSchedulerUserDetails) userDetails)
                        .getId()))
                .thenReturn(new UserDashboardServiceModelView(
                        user.getRegistrationDate().toLocalDate(),
                        new ArrayList<>(),
                        new ArrayList<>()
                ));

        mockMvc
                .perform(get("/").with(user(userDetails)))
                .andExpect(status().isOk());

    }

}
