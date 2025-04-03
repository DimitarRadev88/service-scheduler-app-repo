package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@EnableWireMock(
        @ConfigureWireMock(
                name = "service-scheduler-car-brands-and-models",
                port = (8081)
        )
)
public class GlobalExceptionHandlerIT {

    @InjectWireMock("service-scheduler-car-brands-and-models")
    private WireMockServer wireMockServer;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    private ObjectMapper objectMapper;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testHandleUsernameAlreadyExistsException() throws Exception {
        MockHttpServletRequestBuilder post = createRegisterPost("username", "valid@email");

        mockMvc.perform(post).andExpect(status().is3xxRedirection());

        mockMvc.perform(post).andExpect(status().isConflict())
                .andExpect(model().attributeExists("usernameException"))
                .andExpect(model().attributeExists("userRegister"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testHandeEmailAlreadyExistsException() throws Exception {
        MockHttpServletRequestBuilder post = createRegisterPost("username", "valid@email");

        mockMvc.perform(post).andExpect(status().is3xxRedirection());

        MockHttpServletRequestBuilder postWithSameEmail = createRegisterPost("username1", "valid@email");

        mockMvc.perform(postWithSameEmail).andExpect(status().isConflict())
                .andExpect(model().attributeExists("emailException"))
                .andExpect(model().attributeExists("userRegister"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testHandeAddCarException() throws Exception {
        saveTestUser(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)));

        UserDetails userDetails = userDetailsService.loadUserByUsername("username");

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.post("/vehicles/add/Audi")
                .param("brand", "Audi")
                .param("model", "A4")
                .param("year", "2002")
                .param("vin", "17charactervinnum")
                .param("registration", "12341234")
                .param("category", VehicleCategory.B.name())
                .param("fuelType", FuelType.PETROL.name())
                .param("displacement", "2000")
                .param("oilCapacity", "4.0")
                .param("mileage", "220000")
                .with(user(userDetails))
                .with(csrf());

        mockMvc.perform(post).andExpect(status().is3xxRedirection());

        mockMvc.perform(post).andExpect(status().isConflict())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("vehicle-add-error"));
    }

    @Test
    public void testHandleCarModelAddException() throws Exception {
        saveTestUser(List.of(
                userRoleRepository.findByRole(UserRoleEnumeration.USER),
                userRoleRepository.findByRole(UserRoleEnumeration.ADMIN)
        ));

        UserDetails userDetails = userDetailsService.loadUserByUsername("username");

        wireMockServer.stubFor(post("/models/add/" + "Audi").willReturn(WireMock.status(409)));

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders
                .post("/models/add/" + "Audi")
                .param("brandName", "Audi")
                .param("model", "A4")
                .with(user(userDetails))
                .with(csrf());

        mockMvc.perform(post)
                .andExpect(status().isConflict());
    }

    private void saveTestUser(List<UserRole> roles) {
        userRepository.save(new User(
                        null,
                        "username",
                        "password",
                        "email@email",
                        LocalDateTime.now(),
                        "profilepic",
                        new ArrayList<>(),
                        roles
                )
        );
    }


    private static MockHttpServletRequestBuilder createRegisterPost(String username, String email) {
        return MockMvcRequestBuilders
                .post("/register")
                .param("username", username)
                .param("email", email)
                .param("password", "validpassword")
                .param("confirmPassword", "validpassword")
                .with(csrf());
    }

}
