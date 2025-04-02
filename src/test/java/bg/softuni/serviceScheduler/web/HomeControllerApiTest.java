package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.model.ServiceSchedulerUserDetails;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.SiteStatisticsServiceModelView;
import bg.softuni.serviceScheduler.user.service.dto.UserDashboardServiceModelView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerApiTest {

    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private AuthorizationTestService userDetailsService;

    @BeforeEach
    void setUp() {
        this.userDetailsService = new AuthorizationTestService();
    }

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
        UserDetails userDetails = userDetailsService.getUserDetailsUser();
        User user = userDetailsService.getUser();

        Mockito.when(userService
                        .getUser(((ServiceSchedulerUserDetails) userDetails)
                                .getId()))
                .thenReturn(new UserDashboardServiceModelView(
                        user.getUsername(),
                        user.getRegistrationDate().toLocalDate(),
                        new ArrayList<>(),
                        new ArrayList<>()
                ));

        mockMvc
                .perform(get("/").with(user(userDetails)))
                .andExpect(status().isOk());

    }

}
