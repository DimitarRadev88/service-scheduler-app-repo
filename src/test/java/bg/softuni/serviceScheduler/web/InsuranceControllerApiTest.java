package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carServices.insurance.model.InsuranceValidity;
import bg.softuni.serviceScheduler.carServices.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.CarServiceAddSelectView;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInsuranceAddServiceView;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(InsuranceController.class)
public class InsuranceControllerApiTest {

    @MockitoBean
    private InsuranceService insuranceService;
    @MockitoBean
    private CarService carService;
    @MockitoBean
    private UserService userService;
    private AuthorizationTestService userAuthorization;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private CarInsuranceAddServiceView car;
    private UserDetails userDetails;

    @BeforeEach
    void setup() throws Exception {
        userAuthorization = new AuthorizationTestService();
        user = userAuthorization.getUser();
        userDetails = userAuthorization.getUserDetailsUser();

        car = new CarInsuranceAddServiceView(
                UUID.randomUUID(),
                "Make and Model",
                FuelType.PETROL,
                2000,
                "17charactervinnum",
                "CARREGNN"
        );

    }

    @Test
    public void testGetInsuranceAddViewReturnsCorrectView() throws Exception {
        UserWithCarsInfoAddServiceView user = new UserWithCarsInfoAddServiceView(
                this.user.getId(),
                new ArrayList<>()
        );
        Mockito
                .when(userService.getUserWithCarsInfoAddServiceView(this.user.getId()))
                .thenReturn(user);

        mockMvc.perform(get("/insurances/add").with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("insurance-add"))
                .andExpect(model().attributeExists("insuranceAdd"))
                .andExpect(model().attribute("userId", this.user.getId()))
                .andExpect(model().attribute("user", user));
    }

    @Test
    public void testGetInsuranceAddViewWithVehicleInformationReturnsCorrectView() throws Exception {
        UserWithCarsInfoAddServiceView user = new UserWithCarsInfoAddServiceView(
                this.user.getId(),
                new ArrayList<>(List.of(new CarServiceAddSelectView(UUID.randomUUID(), "make and model")))
        );

        Mockito
                .when(userService.getUserWithCarsInfoAddServiceView(this.user.getId()))
                .thenReturn(user);
        Mockito
                .when(carService.getCarInsuranceAddServiceView(car.id()))
                .thenReturn(car);

        mockMvc.perform(get("/insurances/add/" + car.id()).with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(view().name("insurance-add-with-selected-vehicle"))
                .andExpect(model().attributeExists("insuranceAdd"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("userId", this.user.getId()))
                .andExpect(model().attribute("carInfo", car));
    }

    @Test
    public void testSaveInsuranceAddShouldPostWithValidInsuranceAddModel() throws Exception {

        InsuranceAddBindingModel addBindingModel = new InsuranceAddBindingModel(
                "123",
                LocalDate.now(),
                InsuranceValidity.MONTHLY,
                new BigDecimal("0.5")
        );

        mockMvc.perform(post("/insurances/add/" + car.id())
                        .param("companyName", addBindingModel.companyName())
                        .param("startDate", addBindingModel.startDate().toString())
                        .param("insuranceValidityPeriod", addBindingModel.insuranceValidityPeriod().name())
                        .param("cost", addBindingModel.cost().toString())
                        .with(user(userDetails)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + car.id()));
    }

    @Test
    public void testSaveInsuranceAddShouldRedirectWithErrorsWhenInsuranceAddModelIsInvalid() throws Exception {

        String uriTemplate = String.format("/insurances/add/%s",
                car.id());

        mockMvc.perform(post(uriTemplate)
                        .with(user(userDetails)).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("insuranceAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.insuranceAdd"))
                .andExpect(flash().attributeCount(2))
                .andExpect(redirectedUrl("/insurances/add/" + car.id()));
    }


}
