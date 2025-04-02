package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

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
    @Autowired
    AuthorizationTestService authorizationService;
    private ObjectMapper objectMapper;


    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testHandleUsernameAlreadyExistsException() throws Exception {
        MockHttpServletRequestBuilder post = createPost("username", "valid@email");

        mockMvc.perform(post).andExpect(status().is3xxRedirection());

        mockMvc.perform(post).andExpect(status().isConflict())
                .andExpect(model().attributeExists("usernameException"))
                .andExpect(model().attributeExists("userRegister"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testHandeEmailAlreadyExistsException() throws Exception {
        MockHttpServletRequestBuilder post = createPost("username", "valid@email");

        mockMvc.perform(post).andExpect(status().is3xxRedirection());

        MockHttpServletRequestBuilder postWithSameEmail = createPost("username1", "valid@email");

        mockMvc.perform(postWithSameEmail).andExpect(status().isConflict())
                .andExpect(model().attributeExists("emailException"))
                .andExpect(model().attributeExists("userRegister"))
                .andExpect(view().name("register"));
    }

    @Test
    public void testHandleCarModelAddException() throws Exception {
        wireMockServer.stubFor(post("/models/add/" + "Audi").willReturn(WireMock.status(409)));

                MockHttpServletRequestBuilder post = MockMvcRequestBuilders
                        .post("/models/add/" + "Audi")
                        .param("brandName", "Audi")
                        .param("model", "A4")
                        .with(user(authorizationService.getUserDetailsAdmin()))
                        .with(csrf());

        mockMvc.perform(post)
                .andExpect(status().isConflict());
    }


    private static MockHttpServletRequestBuilder createPost(String username, String email) {
        return MockMvcRequestBuilders
                .post("/register")
                .param("username", username)
                .param("email", email)
                .param("password", "validpassword")
                .param("confirmPassword", "validpassword");
    }

}
