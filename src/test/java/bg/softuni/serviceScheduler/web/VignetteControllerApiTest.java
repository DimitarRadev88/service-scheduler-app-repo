package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carServices.vignette.model.VignetteValidity;
import bg.softuni.serviceScheduler.carServices.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.carServices.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(VignetteController.class)
public class VignetteControllerApiTest {

    @MockitoBean
    private VignetteService vignetteService;
    @MockitoBean
    private CarService carService;
    @MockitoBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    private AuthorizationTestService userAuthorization;
    private User user;
    private CarVignetteAddServiceView car;
    private UserDetails userDetails;

    @BeforeEach
    void setup() throws Exception {
        userAuthorization = new AuthorizationTestService();
        user = userAuthorization.getUser();
        userDetails = userAuthorization.getUserDetailsUser();

        car = new CarVignetteAddServiceView(
                UUID.randomUUID(),
                "Make and Model",
                "CARREGNN"
        );

    }

    @Test
    public void testGetVignetteAddViewReturnsCorrectView() throws Exception {
        UserWithCarsInfoAddServiceView user = new UserWithCarsInfoAddServiceView(
                this.user.getId(),
                new ArrayList<>()
        );

        when(userService
                .getUserWithCarsInfoAddServiceView(this.user.getId()))
                .thenReturn(user);

        mvc.perform(get("/vignettes/add").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("vignette-add"))
                .andExpect(model().attributeExists("vignetteAdd"))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void testGetVignetteAddWithVehicleInformationReturnsCorrectView() throws Exception {
        UserWithCarsInfoAddServiceView user = new UserWithCarsInfoAddServiceView(
                this.user.getId(),
                new ArrayList<>()
        );

        when(userService.getUserWithCarsInfoAddServiceView(this.user.getId()))
                .thenReturn(user);

        when(carService.getCarVignetteAddServiceView(car.id()))
                .thenReturn(car);

        mvc.perform(get("/vignettes/add/" + car.id()).with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("vignette-add-with-selected-vehicle"))
                .andExpect(model().attributeExists("vignetteAdd"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("carInfo", car));
    }

    @Test
    public void testPostVignetteAddRedirectsWithErrorsWhenBindingModelIsInvalid() throws Exception {
        String url = String.format("/vignettes/add/%s?startDate=%s&validity=%s",
                car.id(), null, null);

        mvc.perform(post(url).with(user(userDetails)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vignettes/add/" + car.id()))
                .andExpect(flash().attributeExists("vignetteAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.vignetteAdd"))
                .andExpect(flash().attributeCount(2));
    }

    @Test
    public void testPostVignetteAddRedirectsToVehiclesWhenBindingModelIsValid() throws Exception {
        VignetteAddBindingModel vignetteAdd = new VignetteAddBindingModel(LocalDate.now(), VignetteValidity.WEEKEND);
        String url = String.format("/vignettes/add/%s?startDate=%s&validity=%s",
                car.id(), vignetteAdd.startDate(), vignetteAdd.validity());

        mvc.perform(post(url).with(user(userDetails)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + car.id()))
                .andExpect(flash().attributeCount(0));
    }

}
