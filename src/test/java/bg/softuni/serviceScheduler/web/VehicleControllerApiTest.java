package bg.softuni.serviceScheduler.web;

import bg.softuni.serviceScheduler.carModel.service.CarModelService;
import bg.softuni.serviceScheduler.carServices.oilChange.service.OilChangeService;
import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
public class VehicleControllerApiTest {

    private AuthorizationTestService userAuthorization;
    @MockitoBean
    private CarService carService;
    @MockitoBean
    private CarModelService carModelService;
    @MockitoBean
    private OilChangeService oilChangeService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userAuthorization = new AuthorizationTestService();
    }

    @Test
    public void testGetVehicleAddPageReturnsCorrectPageView() throws Exception {

        mockMvc.perform(get("/vehicles/add").with(user(userAuthorization.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle-add"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attribute("userId", userAuthorization.getUser().getId()));
    }

    @Test
    public void testGetVehicleAddPageWithBrandReturnsCorrectPageView() throws Exception {
        String brand = "BMW";
        CarAddBindingModel vehicleAdd = new CarAddBindingModel(brand);

        mockMvc.perform(get("/vehicles/add/" + brand).with(user(userAuthorization.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle-add-with-brand"))
                .andExpect(model().attributeExists("brands"))
                .andExpect(model().attributeExists("models"))
                .andExpect(model().attribute("vehicleAdd", vehicleAdd))
                .andExpect(model().attribute("userId", userAuthorization.getUser().getId()));
    }

    @Test
    public void testAddNewVehicleWithBrandRedirectsBackWhenInvalidCarAddBingingModel() throws Exception {
        CarAddBindingModel vehicleAdd = new CarAddBindingModel("BMW");

        String urlTemplate = String.format("""
                        /vehicles/add/%s?brand=%s&model=%s&trim=%s&year=%s&vin=%s&registration=%s
                        &category=%s&fuelType=%s&displacement=%s&oilCapacity=%s&mileage=%s&oilFilterNumber=%s
                        """,
                vehicleAdd.brand(), vehicleAdd.brand(), vehicleAdd.model(), vehicleAdd.trim(), " ", vehicleAdd.vin(),
                vehicleAdd.registration(), " ", " ",
                vehicleAdd.displacement(), vehicleAdd.oilCapacity(), vehicleAdd.mileage(), vehicleAdd.oilFilterNumber());

        mockMvc.perform(post(urlTemplate).with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/add/" + vehicleAdd.brand()))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("vehicleAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.vehicleAdd"));
    }

    @Test
    public void testAddNewVehicleWithBrandRedirectsToVehicleWhenCarAddBindingModelIsValid() throws Exception {
        CarAddBindingModel vehicleAdd = new CarAddBindingModel(
                "BMW", "120", "", Year.now(),
                "17charactervinnum", "1234567",
                VehicleCategory.B, FuelType.PETROL, 2000,
                5.0, 130000, ""
        );

        UUID carId = UUID.randomUUID();

        when(carService.doAdd(Mockito.any(), Mockito.any()))
                .thenReturn(carId);

        String urlTemplate = String.format("""
                        /vehicles/add/%s?brand=%s&model=%s&trim=%s&year=%s&vin=%s&registration=%s
                        &category=%s&fuelType=%s&displacement=%s&oilCapacity=%s&mileage=%s&oilFilterNumber=%s
                        """,
                vehicleAdd.brand(), vehicleAdd.brand(), vehicleAdd.model(), vehicleAdd.trim(), vehicleAdd.year().toString(),
                vehicleAdd.vin(), vehicleAdd.registration(), vehicleAdd.category().name(), vehicleAdd.fuelType().name(),
                vehicleAdd.displacement(), vehicleAdd.oilCapacity(), vehicleAdd.mileage(), vehicleAdd.oilFilterNumber());

        mockMvc.perform(post(urlTemplate).with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + carId))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    public void testGetVehicleDetailsReturnsCorrectView() throws Exception {
        CarInfoServiceViewModel car = new CarInfoServiceViewModel(
                UUID.randomUUID(),
                "make and model",
                "17charactervinnum",
                "12345678",
                new LastServicesServiceViewModel(new CarOilChangeDateAndIdServiceViewModel(UUID.randomUUID(), LocalDate.now()),
                        new InsurancePaymentDateAndIdServiceViewModel(UUID.randomUUID(), LocalDate.now(), false, false),
                        new VignetteDateAndIdServiceViewModel(UUID.randomUUID(), LocalDate.now(), false, false)),
                new CarInfoEngineViewModel(
                        UUID.randomUUID(), FuelType.PETROL, 2000, 10000,
                        100, 10000, 10000
                )
        );

        when(carService.getCarInfoServiceViewModel(car.id()))
                .thenReturn(car);


        mockMvc.perform(get("/vehicles/" + car.id()).with(user(userAuthorization.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle-info"))
                .andExpect(model().attribute("userId", userAuthorization.getUser().getId()))
                .andExpect(model().attributeExists("carInfo"))
                .andExpect(model().attributeExists("engineMileageAdd"));
    }

    @Test
    public void testChangeVehicleMileageRedirectsBackToVehicleDetailsWithErrorsWhenEngineMileageAddBindingModelIsInvalid() throws Exception {
        UUID carId = UUID.randomUUID();

        mockMvc.perform(put("/vehicles/" + carId + "/add-mileage")
                        .with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + carId))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("engineMileageAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.engineMileageAdd"));
    }

    @Test
    public void testChangeVehicleMileageRedirectsToVehicleDetailsWhenEngineMileageAddBindingModelValid() throws Exception {
        UUID carId = UUID.randomUUID();

        mockMvc.perform(put("/vehicles/" + carId + "/add-mileage?oldMileage=1000&newMileage=10001")
                        .with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + carId))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    public void testGetAddOilChangeViewReturnsCorrectView() throws Exception {

        CarOilChangeAddServiceViewModel carView = new CarOilChangeAddServiceViewModel(
                UUID.randomUUID(),
                "make and model",
                "17charactervinnum"
        );
        EngineOilChangeServiceViewModel engine = new EngineOilChangeServiceViewModel(
                UUID.randomUUID(),
                FuelType.DIESEL.name(),
                2000,
                4.3,
                "oil filter",
                100000,
                carView
        );
        when(carService.getEngineOilChangeAddViewModel(carView.id()))
                .thenReturn(engine);

        mockMvc.perform(get("/vehicles/engines/" + carView.id() + "/oil-changes/add")
                        .with(user(userAuthorization.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle-oil-change-add"))
                .andExpect(model().attribute("userId", userAuthorization.getUser().getId()))
                .andExpect(model().attribute("engineView", engine))
                .andExpect(model().attributeExists("oilChangeAdd"));
    }

    @Test
    public void testAddOilChangePostRedirectsBackWithErrorsWhenOilChangeAddBindingModelIsInvalid() throws Exception {
        UUID engineId = UUID.randomUUID();

        String uriTemplate = String.format("/vehicles/engines/%s/oil-changes/add", engineId);
        mockMvc.perform(post(uriTemplate).with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:" + uriTemplate))
                .andExpect(flash().attributeCount(2))
                .andExpect(flash().attributeExists("oilChangeAdd"))
                .andExpect(flash().attributeExists("org.springframework.validation.BindingResult.oilChangeAdd"));
    }

    @Test
    public void testAddOilChangePostToVehicleViewWhenOilChangeAddBindingModelValid() throws Exception {
        UUID engineId = UUID.randomUUID();

        UUID carId = UUID.randomUUID();

        String uriTemplate = String.format(
                "/vehicles/engines/%s/oil-changes/add?changeDate=%s&changeMileage=%s&changeInterval=%s&cost=%s",
                engineId,
                LocalDate.now(),
                "100000",
                "10000",
                "100");

        when(oilChangeService.doAdd(Mockito.any(), Mockito.any()))
                .thenReturn(carId);

        mockMvc.perform(post(uriTemplate).with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/vehicles/" + carId))
                .andExpect(flash().attributeCount(0));
    }

    @Test
    public void testDeleteVehicleRedirectsHome() throws Exception {
        UUID carId = UUID.randomUUID();

        mockMvc.perform(delete("/vehicles/" + carId)
                        .with(user(userAuthorization.getUserDetailsUser())).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    public void testGetAllServicesViewReturnsCorrectView() throws Exception {
        when(carService.getAllServicesCostByUser(userAuthorization.getUser().getId()))
                .thenReturn(BigDecimal.TEN);

        mockMvc.perform(get("/vehicles/services").with(user(userAuthorization.getUserDetailsUser())))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicle-all-services"))
                .andExpect(model().attributeExists("services"))
                .andExpect(model().attributeExists("allServicesCost"));
    }

}
