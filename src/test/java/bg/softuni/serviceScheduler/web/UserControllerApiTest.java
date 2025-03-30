package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.UserEditProfileServiceModel;
import bg.softuni.serviceScheduler.user.service.dto.UserProfileViewServiceModel;
import bg.softuni.serviceScheduler.web.dto.UserProfileEditBindingModel;
import bg.softuni.serviceScheduler.web.dto.UserRegisterBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    private UserDetailsTestService userDetailsService;

    @BeforeEach
    public void setup() {
        userDetailsService = new UserDetailsTestService();
    }

    @Test
    public void testGetLoginViewReturnsLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRegisterViewWithLoggedInUserRedirectsHome() throws Exception {
        mockMvc.perform(get("/register").with(user(userDetailsService.getUserDetailsUser())))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testGetRegisterViewReturnsRegister() throws Exception {

        mockMvc.perform(get("/register").with(user("guest")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("userRegister"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testPostRegisterRedirectsBackWhenInvalidUserRegisterBindingModel() throws Exception {

        mockMvc.perform(post("/register").with(user("guest")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/register"))
                .andExpect(flash().attributeExists("userRegister"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userRegister"))
                .andExpect(flash().attributeCount(2));
    }

    @Test
    public void testPostRegisterRedirectsToLoginWithValidUserRegisterBindingModel() throws Exception {
        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel(
                "username", "email@email", "password", "password"
        );
        String urlTemplate = String.format("/register?username=%s&email=%s&password=%s&confirmPassword=%s",
                userRegisterBindingModel.username(), userRegisterBindingModel.email(),
                userRegisterBindingModel.password(), userRegisterBindingModel.confirmPassword());

        when(userService.doRegister(userRegisterBindingModel))
                .thenReturn("username");

        mockMvc.perform(post(urlTemplate).with(user("guest")).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"))
                .andExpect(flash().attribute("username", "username"))
                .andExpect(flash().attributeCount(1));
    }

    @Test
    public void testGetUsersViewWithUserNotAdmin() throws Exception {
        mockMvc.perform(get("/users").with(user(userDetailsService.getUserDetailsAdmin())))
                .andExpect(status().isOk())
                .andExpect(view().name("all-users"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    public void testRemoveAdminRoleRedirectsToUsersViewWhenUserIsAdmin() throws Exception {
        User admin = userDetailsService.getUserAdmin();
        mockMvc.perform(put("/users/change-role/user/" + admin.getId())
                        .with(user(userDetailsService.getUserDetailsAdmin()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"));
    }

    @Test
    public void testRemoveAdminRoleRedirectsWhenUserNotAdmin() throws Exception {
        User admin = userDetailsService.getUserAdmin();
        mockMvc.perform(put("/users/change-role/user/" + admin.getId()).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMakeAdminRoleRedirectsToUsersViewWhenUserIsAdmin() throws Exception {
        User user = userDetailsService.getUser();
        mockMvc.perform(put("/users/change-role/admin/" + user.getId())
                        .with(user(userDetailsService.getUserDetailsAdmin()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/users"));
    }

    @Test
    public void testMakeAdminRoleRedirectsWhenUserNotAdmin() throws Exception {
        User user = userDetailsService.getUser();

        mockMvc.perform(put("/users/change-role/admin/" + user.getId()).with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetProfileViewReturnsCorrectProfileView() throws Exception {
        User user = userDetailsService.getUser();
        UserProfileViewServiceModel userView = new UserProfileViewServiceModel(
                user.getUsername(),
                user.getEmail(),
                user.getRegistrationDate().toLocalDate(),
                user.getProfilePictureURL()
        );
        when(userService.getUserProfileView(user.getId()))
                .thenReturn(userView);

        mockMvc.perform(get("/profile").with(user(userDetailsService.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("userId", user.getId()))
                .andExpect(model().attribute("user", userView));
    }

    @Test

    public void testGetProfileEditViewReturnsCorrectProfileEditView() throws Exception {
        User user = userDetailsService.getUser();

        UserProfileViewServiceModel userView = new UserProfileViewServiceModel(
                user.getUsername(),
                user.getEmail(),
                user.getRegistrationDate().toLocalDate(),
                user.getProfilePictureURL()
        );

        UserEditProfileServiceModel userEdit = new UserEditProfileServiceModel(
                user.getUsername(),
                user.getEmail(),
                user.getProfilePictureURL()
        );
        when(userService.getUserEditProfileServiceModel(user.getId()))
                .thenReturn(userEdit);
        when(userService.getUserProfileView(user.getId()))
                .thenReturn(userView);

        mockMvc.perform(get("/profile/edit").with(user(userDetailsService.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("userEdit"))
                .andExpect(model().attribute("user", userView))
                .andExpect(model().attribute("profilePicture", user.getProfilePictureURL()))
                .andExpect(model().attribute("userId", user.getId()));
    }

    @Test
    public void testEditProfileRedirectsBackWithErrorsWhenInvalidUserProfileEditBindingModel() throws Exception {
        User user = userDetailsService.getUser();


        String urlTemplate = String.format("/profile/%s/edit?username=%s&email=%s&profilePictureUrl=%s",
                user.getId(), "", "", "");

        mockMvc.perform(put(urlTemplate).with(user(userDetailsService.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile/edit"))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.userEdit"))
                .andExpect(flash().attributeExists("userEdit"));
    }

    @Test
    public void testEditProfileRedirectsToProfileViewWithValidUserProfileEditBindingModel() throws Exception {
        User user = userDetailsService.getUser();


        String urlTemplate = String.format("/profile/%s/edit?username=%s&email=%s&profilePictureUrl=%s",
                user.getId(), user.getUsername(), user.getEmail(), user.getProfilePictureURL());

        mockMvc.perform(put(urlTemplate).with(user(userDetailsService.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/profile"))
                .andExpect(flash().attributeCount(0));
    }

}
