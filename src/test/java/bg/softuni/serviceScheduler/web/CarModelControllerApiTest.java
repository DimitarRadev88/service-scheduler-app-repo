package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carModel.service.CarModelService;
import bg.softuni.serviceScheduler.carModel.service.dto.CarBrandNameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarModelController.class)
public class CarModelControllerApiTest {

    private static final List<CarBrandNameDto> BRANDS = List.of(new CarBrandNameDto("Audi"), new CarBrandNameDto("BMW"));
    @MockitoBean
    private CarModelService carModelService;
    @Autowired
    private MockMvc mockMvc;
    private AuthorizationTestService userAuthorization;

    @BeforeEach
    public void setup() {
        userAuthorization = new AuthorizationTestService();
    }

    @Test
    public void testGetAddCarModelViewRedirectsWhenUserIsUnauthorized() throws Exception {
        mockMvc.perform(get("/models/add"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAddCarModelReturnsCorrectViewWhenUserIsAuthorized() throws Exception {
        when(carModelService.getAllBrands())
                .thenReturn(BRANDS);

        mockMvc.perform(get("/models/add").with(user(userAuthorization.getUserDetailsAdmin())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-car-model"))
                .andExpect(model().attributeExists("userId"))
                .andExpect(model().attributeExists("carModelAdd"))
                .andExpect(model().attribute("brands", BRANDS));
    }

    @Test
    public void testgetAddCarModelViewWithBrandRedirectsWhenUserIsUnauthorized() throws Exception {
        String brand = "BMW";
        mockMvc.perform(get("/models/add/" + brand))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAddCarModelViewWithBrandReturnsCorrectViewWhenUserIsAuthorized() throws Exception {
        when(carModelService.getAllBrands())
                .thenReturn(BRANDS);
        CarBrandNameDto brand = BRANDS.getFirst();

        mockMvc.perform(get("/models/add/" + brand).with(user(userAuthorization.getUserDetailsAdmin())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-car-model-with-brand"))
                .andExpect(model().attribute("userId", AuthorizationTestService.USER_ID))
                .andExpect(model().attributeExists("carModelAdd"))
                .andExpect(model().attribute("brands", BRANDS));
    }

    @Test
    public void testPostAddCarModelRedirectsWithErrorsWhenCarModelAddBindingModelIsInvalid() throws Exception {
        String brand = BRANDS.getFirst().name();

        mockMvc.perform(post("/models/add/" + brand)
                        .with(user(userAuthorization.getUserDetailsAdmin()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/models/add/" + brand))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("carModelAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.carModelAdd"));
    }

    @Test
    public void testPostAddCarModelRedirectsHomeWhenCarModelAddBindingModelIsValid() throws Exception {
        String brand = BRANDS.getFirst().name();

        mockMvc.perform(post("/models/add/" + brand)
                        .with(user(userAuthorization.getUserDetailsAdmin()))
                        .param("model", "model")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    public void testGetAddCarBrandViewRedirectsWhenUserIsUnauthorized() throws Exception {
        mockMvc.perform(get("/models/brands/add"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetAddCarBrandReturnsCorrectViewWhenUserIsAuthorized() throws Exception {
        mockMvc.perform(get("/models/brands/add")
                        .with(user(userAuthorization.getUserDetailsAdmin())))
                .andExpect(status().isOk())
                .andExpect(view().name("add-car-brand"))
                .andExpect(model().attribute("userId", AuthorizationTestService.USER_ID))
                .andExpect(model().attributeExists("carBrandAdd"));
    }

    @Test
    public void testPostAddCarBrandRedirectsWithErrorsWhenCarBrandAddBindingModelIsInvalid() throws Exception {
        mockMvc.perform(post("/models/brands/add")
                        .param("name", "")
                        .with(user(userAuthorization.getUserDetailsAdmin()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/models/brands/add"))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("carBrandAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.carBrandAdd"));
    }

    @Test
    public void testPostAddCarBrandRedirectsToAddModelWithNewBrandWhenCarBrandAddBindingModelIsValid() throws Exception {
        String brand = BRANDS.getFirst().name();
        mockMvc.perform(post("/models/brands/add")
                        .param("name", brand)
                        .with(user(userAuthorization.getUserDetailsAdmin()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/models/add/" + brand))
                .andExpect(flash().attributeCount(0));
    }

}
