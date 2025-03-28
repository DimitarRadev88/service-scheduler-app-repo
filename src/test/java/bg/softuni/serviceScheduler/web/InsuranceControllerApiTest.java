package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carServices.insurance.model.InsuranceValidity;
import bg.softuni.serviceScheduler.carServices.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.user.service.dto.CarInsuranceAddSelectView;
import bg.softuni.serviceScheduler.user.service.dto.UserWithCarsInfoAddServiceView;
import bg.softuni.serviceScheduler.user.service.impl.ServiceSchedulerUserDetailsService;
import bg.softuni.serviceScheduler.vehicle.model.Car;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(InsuranceController.class)
public class InsuranceControllerApiTest {

    public static final UUID USER_ID = UUID.randomUUID();
    @MockitoBean
    private InsuranceService insuranceService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private CarService carService;
    @MockitoBean
    private UserService userService;

    ServiceSchedulerUserDetailsService serviceSchedulerUserDetailsService;

    @Autowired
    private MockMvc mockMvc;

    private User user;
    private CarInsuranceAddServiceView car;
    UserDetails userDetails;

    @BeforeEach
    public void setup() throws Exception {
        serviceSchedulerUserDetailsService = new ServiceSchedulerUserDetailsService(userRepository);
        user = new User(
                USER_ID,
                "user",
                "password",
                "email",
                LocalDateTime.now(),
                "profile picture",
                new ArrayList<>(),
                List.of(new UserRole(UUID.randomUUID(), UserRoleEnumeration.USER))
        );

        car = new CarInsuranceAddServiceView(
                UUID.randomUUID(),
                "Make and Model",
                FuelType.PETROL,
                2000,
                "17charactervinnum",
                "CARREGNN"
        );

        Mockito
                .when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        userDetails = serviceSchedulerUserDetailsService.loadUserByUsername("user");
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
                USER_ID,
                new ArrayList<>(List.of(new CarInsuranceAddSelectView(UUID.randomUUID(), "make and model")))
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
                .andExpect(model().attribute("userId", USER_ID))
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

        String uriTemplate = String.format("/insurances/add/%s?companyName=%s&startDate=%s&insuranceValidityPeriod=%s&cost=%s",
               car.id(), addBindingModel.companyName(), addBindingModel.startDate(), addBindingModel.insuranceValidityPeriod().name(), addBindingModel.cost());

        mockMvc.perform(post(uriTemplate)
                        .with(user(userDetails)).with(csrf())
                        .formField("companyName", addBindingModel.companyName())
                        .formField("startDate", addBindingModel.startDate().toString())
                        .formField("insuranceValidityPeriod", addBindingModel.insuranceValidityPeriod().name())
                        .formField("cost", addBindingModel.cost().toString()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void testSaveInsuranceAddShouldRedirectWithInvalidInsuranceAddModel() throws Exception {

        String uriTemplate = String.format("/insurances/add/%s",
                car.id());

        mockMvc.perform(post(uriTemplate)
                        .with(user(userDetails)).with(csrf())
                        .formField("companyName", "")
                        .formField("startDate", "")
                        .formField("insuranceValidityPeriod", "")
                        .formField("cost", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/insurances/add/" + car.id()));
    }


}
