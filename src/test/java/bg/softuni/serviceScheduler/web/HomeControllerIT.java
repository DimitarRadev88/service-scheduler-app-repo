package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carServices.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.SiteStatisticsServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import bg.softuni.serviceScheduler.user.service.impl.ServiceSchedulerUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeControllerIT {

    @Autowired
    private ServiceSchedulerUserDetailsService userDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private OilChangeRepository oilChangeRepository;
    private User user;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(new User(null, "user", "password", "eemai@asd", LocalDateTime.now(), "picture", new ArrayList<>(), new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)))));
        userRepository.save(new User(null, "user1", "password", "eem1ai@asd", LocalDateTime.now(), "picture", new ArrayList<>(), new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)))));
        userRepository.save(new User(null, "user2", "password", "eema2i@asd", LocalDateTime.now(), "picture", new ArrayList<>(), new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)))));
    }

    @Test
    public void testViewHomeReturnsIndexWithStatisticsWhenUserIsLoggedIn() throws Exception {
        SiteStatisticsServiceModelView statistics = new SiteStatisticsServiceModelView(userRepository.count(), oilChangeRepository.count());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("statistics", statistics));

    }

    @Test
    public void testViewHomeReturnsHomeWhenUserIsLoggedIn() throws Exception {
        UserDashboardServiceModelView userView = new UserDashboardServiceModelView(user.getRegistrationDate().toLocalDate(), new ArrayList<>(), new ArrayList<>());

        mockMvc.perform(get("/").with(user(userDetailsService.loadUserByUsername(user.getUsername()))))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attribute("userId", user.getId()))
                .andExpect(model().attribute("user", userView));

    }


}
