package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.carModel.dao.CarModelRepository;
import bg.softuni.serviceScheduler.carModel.model.CarModel;
import bg.softuni.serviceScheduler.carServices.insurance.model.Insurance;
import bg.softuni.serviceScheduler.carServices.insurance.model.InsuranceValidity;
import bg.softuni.serviceScheduler.carServices.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.carServices.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.carServices.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.carServices.vignette.model.Vignette;
import bg.softuni.serviceScheduler.carServices.vignette.model.VignetteValidity;
import bg.softuni.serviceScheduler.carServices.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.carServices.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.dto.CarInsuranceAddSelectView;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.dao.EngineRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.exception.EngineNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.vehicle.service.impl.CarServiceImpl;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class CarServiceTests {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CAR_ID = UUID.randomUUID();
    private static final String CAR_BRAND = "Brand";
    private static final String CAR_MODEL = "Model";
    private static final String CAR_TRIM = "Trim";
    private static final Year CAR_YEAR = Year.of(2000);
    private static final String CAR_VIN = "CARVINNUMBER12345";
    private static final String CAR_REGISTRATION = "BB0000BB";
    private static final VehicleCategory CAR_CATEGORY = VehicleCategory.B;
    private static final UUID CAR_MODEL_ID = UUID.randomUUID();
    private static final UUID ENGINE_ID = UUID.randomUUID();
    private static final FuelType ENGINE_FUEL_TYPE = FuelType.PETROL;
    private static final int ENGINE_DISPLACEMENT = 2000;
    private static final double ENGINE_OIL_CAPACITY = 4.0;
    private static final int ENGINE_MILEAGE = 123000;
    private static final String ENGINE_OIL_FILTER_NUMBER = "Oil filter number";
    private static final UUID OIL_CHANGE_ID = UUID.randomUUID();
    private static final Integer OIL_CHANGE_MILEAGE = ENGINE_MILEAGE - 1000;
    private static final Integer OIL_CHANGE_INTERVAL = 10000;
    private static final LocalDate OIL_CHANGE_DATE = LocalDate.now().minusDays(6);
    private static final LocalDate OIL_CHANGE_ADD_DATE = LocalDate.now().minusDays(2);
    private static final UUID INSURANCE_ID = UUID.randomUUID();
    private static final String INSURANCE_COMPANY = "Insurance Company";
    private static final InsuranceValidity INSURANCE_VALIDITY = InsuranceValidity.MONTHLY;
    private static final LocalDate INSURANCE_START_DATE = LocalDate.now().minusDays(5);
    private static final LocalDate INSURANCE_END_DATE = INSURANCE_START_DATE.plusDays(INSURANCE_VALIDITY.getDays());
    private static final LocalDate INSURANCE_ADD_DATE = LocalDate.now().minusDays(1);
    private static final Boolean INSURANCE_IS_VALID = INSURANCE_END_DATE.isAfter(LocalDate.now());
    private static final UUID VIGNETTE_ID = UUID.randomUUID();
    private static final VignetteValidity VIGNETTE_VALIDITY = VignetteValidity.WEEKEND;
    private static final LocalDate VIGNETTE_START_DATE = LocalDate.now().minusDays(1);
    private static final LocalDate VIGNETTE_END_DATE = LocalDate.now().plusDays(VIGNETTE_VALIDITY.getDays());
    private static final LocalDate VIGNETTE_ADD_DATE = LocalDate.now();
    private static final Boolean VIGNETTE_IS_VALID = VIGNETTE_END_DATE.isAfter(LocalDate.now());
    private final CarRepository carRepository = Mockito.mock(CarRepository.class);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final CarModelRepository carModelRepository = Mockito.mock(CarModelRepository.class);
    private final OilChangeRepository oilChangeRepository = Mockito.mock(OilChangeRepository.class);
    private final EngineRepository engineRepository = Mockito.mock(EngineRepository.class);
    private final InsuranceService insuranceService = Mockito.mock(InsuranceService.class);
    private final VignetteService vignetteService = Mockito.mock(VignetteService.class);
    private static final BigDecimal SERVICE_COST = BigDecimal.ONE;

    private static final List<CarDashboardServicesDoneViewServiceModel> ALL_SERVICES = List.of(
            new CarDashboardServicesDoneViewServiceModel("Vignette", VIGNETTE_ADD_DATE, SERVICE_COST),
            new CarDashboardServicesDoneViewServiceModel("Insurance", INSURANCE_ADD_DATE, SERVICE_COST),
            new CarDashboardServicesDoneViewServiceModel("Oil change", OIL_CHANGE_ADD_DATE, SERVICE_COST)
    );

    private CarService carService;
    private User user;
    private Car car;
    private CarAddBindingModel carAdd;
    private CarModel carModel;
    private Engine engine;
    private OilChange oilChange;
    private Insurance insurance;
    private Vignette vignette;
    @Captor
    ArgumentCaptor<Car> carCaptor;
    @Captor
    ArgumentCaptor<Engine> engineCaptor;

    @BeforeEach
    public void setUp() {
        this.carService = new CarServiceImpl(carRepository, userRepository, carModelRepository, oilChangeRepository, engineRepository, insuranceService, vignetteService);
        this.user = new User();
        this.user.setId(USER_ID);
        this.engine = new Engine(ENGINE_ID, ENGINE_DISPLACEMENT, ENGINE_FUEL_TYPE, ENGINE_OIL_CAPACITY,
                ENGINE_MILEAGE, ENGINE_OIL_FILTER_NUMBER, this.car, new ArrayList<>());
        this.oilChange = new OilChange(OIL_CHANGE_ID, this.engine, SERVICE_COST, OIL_CHANGE_ADD_DATE, OIL_CHANGE_MILEAGE, OIL_CHANGE_INTERVAL, OIL_CHANGE_DATE);
        this.engine.getOilChanges().add(oilChange);
        this.carModel = new CarModel(CAR_MODEL_ID, CAR_BRAND, CAR_MODEL, new ArrayList<>());
        this.insurance = new Insurance(INSURANCE_ID, INSURANCE_ADD_DATE, SERVICE_COST, INSURANCE_COMPANY, INSURANCE_START_DATE, INSURANCE_END_DATE, INSURANCE_VALIDITY, INSURANCE_IS_VALID, this.car);
        this.vignette = new Vignette(VIGNETTE_ID, VIGNETTE_ADD_DATE, SERVICE_COST, VIGNETTE_START_DATE, VIGNETTE_END_DATE, VIGNETTE_VALIDITY, VIGNETTE_IS_VALID, this.car);
        this.car = new Car(CAR_ID, this.carModel, this.engine, CAR_VIN, CAR_YEAR, CAR_CATEGORY, this.user, CAR_REGISTRATION, List.of(this.insurance), List.of(this.vignette));
        this.engine.setCar(car);
        carAdd = new CarAddBindingModel(CAR_BRAND, CAR_MODEL, CAR_TRIM, CAR_YEAR, CAR_VIN, CAR_REGISTRATION, CAR_CATEGORY, ENGINE_FUEL_TYPE, ENGINE_DISPLACEMENT, ENGINE_OIL_CAPACITY, ENGINE_MILEAGE, ENGINE_OIL_FILTER_NUMBER);
        carCaptor = ArgumentCaptor.forClass(Car.class);
        engineCaptor = ArgumentCaptor.forClass(Engine.class);
    }

    @Test
    public void testGetAllServicesCostByUserReturnsCorrectServicesCostWhenNoServicesArePresent() {
        Mockito
                .when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        Mockito
                .when(insuranceService.getSumInsuranceCostByUserId(USER_ID))
                .thenReturn(BigDecimal.ZERO);
        Mockito
                .when(vignetteService.getSumVignetteCostByUserId(USER_ID))
                .thenReturn(BigDecimal.ZERO);
        Mockito
                .when(oilChangeRepository.getSumOilChangesCostByUserId(USER_ID))
                .thenReturn(null);

        BigDecimal expected = BigDecimal.ZERO;

        assertEquals(expected, carService.getAllServicesCostByUser(USER_ID));
    }

    @Test
    public void testGetAllServicesCostByUserReturnsCorrectServicesCost() {
        Mockito
                .when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        Mockito
                .when(insuranceService.getSumInsuranceCostByUserId(USER_ID))
                .thenReturn(this.insurance.getCost());
        Mockito
                .when(vignetteService.getSumVignetteCostByUserId(USER_ID))
                .thenReturn(this.vignette.getCost());
        Mockito
                .when(oilChangeRepository.getSumOilChangesCostByUserId(USER_ID))
                .thenReturn(this.oilChange.getCost());
        BigDecimal expected = this.insurance.getCost().add(this.vignette.getCost()).add(this.oilChange.getCost());

        assertEquals(expected, carService.getAllServicesCostByUser(USER_ID));
    }

    @Test
    public void testGetAllServicesCostByUserThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> carService.getAllServicesCostByUser(USER_ID));
    }

    @Test
    public void testGetCarVignetteAddServiceViewReturnsCorrectCarVignetteAddServiceView() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        CarVignetteAddServiceView expected = new CarVignetteAddServiceView(
                CAR_ID,
                CAR_BRAND + " " + CAR_MODEL,
                CAR_REGISTRATION
        );

        CarVignetteAddServiceView actual = carService.getCarVignetteAddServiceView(CAR_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetCarVignetteAddServiceViewThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarVignetteAddServiceView(CAR_ID));
    }

    @Test
    public void testDoDeleteDeletes() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(carRepository)
                .deleteById(CAR_ID);

        carService.doDelete(CAR_ID);
    }

    @Test
    public void testDoDeleteThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.existsById(CAR_ID))
                .thenReturn(false);

        assertThrows(CarNotFoundException.class, () -> carService.doDelete(CAR_ID));
    }

    @Test
    public void testAddMileageAdds() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        int newMileage = car.getEngine().getMileage() + 10000;
        EngineMileageAddBindingModel mileageAddBindingModel = new EngineMileageAddBindingModel(car.getEngine().getMileage(), newMileage);

        carService.doAddMileage(mileageAddBindingModel, CAR_ID);

        verify(carRepository).save(carCaptor.capture());

        Car saved = carCaptor.getValue();

        assertEquals(newMileage, saved.getEngine().getMileage(), "wrong mileage");
    }

    @Test
    public void testDoAddMileageThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.doAddMileage(new EngineMileageAddBindingModel(null, null), CAR_ID));
    }

    @Test
    public void testGetEngineOilChangeViewModelReturnsCorrectViewModel() {
        Mockito
                .when(engineRepository.findById(ENGINE_ID))
                .thenReturn(Optional.of(this.engine));

        EngineOilChangeServiceViewModel expected = new EngineOilChangeServiceViewModel(
                ENGINE_ID,
                "Petrol",
                ENGINE_DISPLACEMENT,
                ENGINE_OIL_CAPACITY,
                ENGINE_OIL_FILTER_NUMBER,
                ENGINE_MILEAGE,
                new CarOilChangeAddServiceViewModel(
                        CAR_ID,
                        CAR_BRAND + " " + CAR_MODEL,
                        CAR_VIN
                )
        );

        EngineOilChangeServiceViewModel actual = carService.getEngineOilChangeAddViewModel(ENGINE_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetEngineOilChangeAddViewModelThroesWhenEngineNotFound() {
        Mockito
                .when(engineRepository.findById(ENGINE_ID))
                .thenReturn(Optional.empty());

        assertThrows(EngineNotFoundException.class, () -> carService.getEngineOilChangeAddViewModel(ENGINE_ID));
    }

    @Test
    public void testGetCarInfoServiceViewModelReturns() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));
        Mockito
                .when(oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(ENGINE_ID))
                .thenReturn(Optional.of(oilChange));

        CarInfoServiceViewModel expected = new CarInfoServiceViewModel(
                CAR_ID,
                CAR_BRAND + " " + CAR_MODEL,
                CAR_VIN,
                CAR_REGISTRATION,
                new LastServicesServiceViewModel(
                        new CarOilChangeDateAndIdServiceViewModel(
                                OIL_CHANGE_ID,
                                OIL_CHANGE_DATE
                        ),
                        new InsurancePaymentDateAndIdServiceViewModel(
                                INSURANCE_ID,
                                INSURANCE_ADD_DATE,
                                INSURANCE_END_DATE.isBefore(LocalDate.now().plusWeeks(1)),
                                INSURANCE_END_DATE.isBefore(LocalDate.now())
                        ),
                        new VignetteDateAndIdServiceViewModel(
                                VIGNETTE_ID,
                                VIGNETTE_ADD_DATE,
                                VIGNETTE_END_DATE.isBefore(LocalDate.now().plusDays(1)),
                                VIGNETTE_END_DATE.isBefore(LocalDate.now())
                        )
                ),
                new CarInfoEngineViewModel(
                        ENGINE_ID,
                        ENGINE_FUEL_TYPE,
                        ENGINE_DISPLACEMENT,
                        ENGINE_MILEAGE,
                        (ENGINE_MILEAGE - OIL_CHANGE_MILEAGE) * 100 / OIL_CHANGE_INTERVAL,
                        OIL_CHANGE_INTERVAL,
                        OIL_CHANGE_MILEAGE
                )
        );

        CarInfoServiceViewModel actual = carService.getCarInfoServiceViewModel(CAR_ID);

        assertEquals(expected, actual);

    }

    @Test
    public void testGetCarInfoServiceViewModelThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarInfoServiceViewModel(CAR_ID));
    }

    @Test
    public void testGetCarInsuranceAddServiceViewReturnsCorrectView() {
        CarInsuranceAddServiceView expected = new CarInsuranceAddServiceView(
                CAR_ID,
                CAR_BRAND + " " + CAR_MODEL,
                ENGINE_FUEL_TYPE,
                ENGINE_DISPLACEMENT,
                CAR_VIN,
                CAR_REGISTRATION
        );

        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        CarInsuranceAddServiceView actual = carService.getCarInsuranceAddServiceView(CAR_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetCarInsuranceAddServiceViewThrowsWhenCarNotFound() {
        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> carService.getCarInsuranceAddServiceView(CAR_ID));
    }

    @Test
    public void testDoAddCreatesNewCarModel() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(this.user));
        Mockito
                .when(carModelRepository.findCarModelByBrandNameAndModelName(CAR_BRAND, CAR_MODEL))
                .thenReturn(Optional.empty());

        Mockito
                .when(carRepository.save(Mockito.any(Car.class)))
                .thenReturn(car);

        carService.doAdd(carAdd, USER_ID);

        verify(carRepository).save(carCaptor.capture());
        Car savedCar = carCaptor.getValue();

        assertEquals(CAR_BRAND, savedCar.getModel().getBrandName());
        assertEquals(CAR_MODEL, savedCar.getModel().getModelName());

    }

    @Test
    public void testDoAddAdds() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(this.user));
        Mockito
                .when(carModelRepository.findCarModelByBrandNameAndModelName(CAR_BRAND, CAR_MODEL))
                .thenReturn(Optional.of(carModel));

        Mockito
                .when(carRepository.save(Mockito.any(Car.class)))
                .thenReturn(car);

        carService.doAdd(carAdd, USER_ID);

        verify(carRepository).save(carCaptor.capture());
        Car savedCar = carCaptor.getValue();
        verify(engineRepository).save(engineCaptor.capture());
        Engine savedEngine = engineCaptor.getValue();

        assertEquals(user, savedCar.getUser(), "wrong user");
        assertEquals(carModel, savedCar.getModel(), "wrong model");
        assertEquals(CAR_CATEGORY, savedCar.getCategory(), "wrong category");
        assertEquals(CAR_VIN, savedCar.getVin(), "wrong vin");
        assertEquals(CAR_YEAR, savedCar.getYearOfProduction(), "wrong year");
        assertEquals(CAR_REGISTRATION, savedCar.getRegistration(), "wrong registration");

        assertEquals(ENGINE_DISPLACEMENT, savedEngine.getDisplacement(), "wrong displacement");
        assertEquals(ENGINE_MILEAGE, savedEngine.getMileage(), "wrong mileage");
        assertEquals(ENGINE_OIL_CAPACITY, savedEngine.getOilCapacity(), "wrong oil capacity");
        assertEquals(ENGINE_FUEL_TYPE, savedEngine.getFuelType(), "wrong fuel type");
        assertEquals(savedCar, savedEngine.getCar(), "wrong car");

    }

    @Test
    public void testDoAddThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> carService.doAdd(carAdd, UUID.randomUUID()));
    }

    @Test
    public void testGetAllServicesByUserShouldReturn() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(this.user));
        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(List.of(this.car));

        List<CarDashboardServicesDoneViewServiceModel> result = carService.getAllServicesByUser(USER_ID);
        for (int i = 0; i < ALL_SERVICES.size(); i++) {
            assertEquals(ALL_SERVICES.get(i), result.get(i));
        }
    }

    @Test
    public void testGetCarInfoServiceViewModelWithEmptyServices() {
        car.getEngine().setOilChanges(new ArrayList<>());
        car.setVignettes(new ArrayList<>());
        car.setInsurances(new ArrayList<>());

        CarInfoServiceViewModel carInfo = new CarInfoServiceViewModel(
                CAR_ID,
                carModel.getBrandName() + " " + carModel.getModelName(),
                CAR_VIN,
                CAR_REGISTRATION,
                new LastServicesServiceViewModel(
                        new CarOilChangeDateAndIdServiceViewModel(null, null),
                        new InsurancePaymentDateAndIdServiceViewModel(null, null, true, true),
                        new VignetteDateAndIdServiceViewModel(null, null, true, true)
                ),
                new CarInfoEngineViewModel(
                        ENGINE_ID,
                        ENGINE_FUEL_TYPE,
                        ENGINE_DISPLACEMENT,
                        ENGINE_MILEAGE,
                        100,
                        0,
                        0
                        )
        );

        Mockito
                .when(carRepository.findById(CAR_ID))
                .thenReturn(Optional.of(car));

        CarInfoServiceViewModel viewModel = carService.getCarInfoServiceViewModel(CAR_ID);

        assertEquals(carInfo, viewModel);

    }

    @Test
    public void testGetAllServicesByUserShouldReturnEmptyListWhenNoServicesFound() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.of(this.user));
        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(new ArrayList<>());

        assertTrue(carService.getAllServicesByUser(USER_ID).isEmpty());
    }

    @Test
    public void testGetAllServicesByUserShouldThrowWhenUserNotFound() {
        Mockito
                .when(userRepository.findById(USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> carService.getAllServicesByUser(USER_ID));
    }

    @Test
    public void testGetAllCarDashboardServiceViewModelsByUserThrowsWhenUserNotFound() {
        Mockito
                .when(userRepository.existsById(USER_ID))
                .thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> carService.getAllCarDashboardServiceViewModelsByUser(USER_ID));
    }

    @Test
    public void testGetAllCarDashboardServiceViewModelsByUserReturnsCorrectValuesWhenNoServiceIsNeededAndHaveServicesDone() {
        Mockito.
                when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(new ArrayList<>(
                        List.of(
                                car
                        )
                ));
        Mockito.
                when(insuranceService.hasActiveInsurance(CAR_ID))
                .thenReturn(true);
        Mockito
                .when(vignetteService.hasActiveVignette(CAR_ID))
                .thenReturn(true);
        Mockito
                .when(oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(ENGINE_ID))
                .thenReturn(Optional.of(oilChange));

        List<CarDashboardViewServiceModel> expected = new ArrayList<>(
                List.of(
                        new CarDashboardViewServiceModel(CAR_ID, CAR_BRAND, CAR_MODEL, CAR_VIN, new BigDecimal("3"), false)
                )
        );

        List<CarDashboardViewServiceModel> actual = carService.getAllCarDashboardServiceViewModelsByUser(USER_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllCarDashboardServiceViewModelsByUserReturnsCorrectValuesWhenServiceIsNeededAndHaveServicesDone() {
        Mockito.
                when(userRepository.existsById(USER_ID))
                .thenReturn(true);
        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(new ArrayList<>(
                        List.of(
                                car
                        )
                ));
        Mockito.
                when(insuranceService.hasActiveInsurance(CAR_ID))
                .thenReturn(false);
        Mockito
                .when(vignetteService.hasActiveVignette(CAR_ID))
                .thenReturn(false);
        Mockito
                .when(oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(ENGINE_ID))
                .thenReturn(Optional.of(oilChange));

        List<CarDashboardViewServiceModel> expected = new ArrayList<>(
                List.of(
                        new CarDashboardViewServiceModel(CAR_ID, CAR_BRAND, CAR_MODEL, CAR_VIN, new BigDecimal("3"), true)
                )
        );

        List<CarDashboardViewServiceModel> actual = carService.getAllCarDashboardServiceViewModelsByUser(USER_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllCarDashboardServiceViewModelsByUserReturnsCorrectValuesWhenServiceIsNeededAndNoServicesDone() {
        Mockito.
                when(userRepository.existsById(USER_ID))
                .thenReturn(true);

        car.setInsurances(new ArrayList<>());
        car.setVignettes(new ArrayList<>());
        car.getEngine().setOilChanges(new ArrayList<>());

        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(new ArrayList<>(
                        List.of(
                                car
                        )
                ));
        Mockito.
                when(insuranceService.hasActiveInsurance(CAR_ID))
                .thenReturn(false);
        Mockito
                .when(vignetteService.hasActiveVignette(CAR_ID))
                .thenReturn(false);
        Mockito
                .when(oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(ENGINE_ID))
                .thenReturn(Optional.empty());

        List<CarDashboardViewServiceModel> expected = new ArrayList<>(
                List.of(
                        new CarDashboardViewServiceModel(CAR_ID, CAR_BRAND, CAR_MODEL, CAR_VIN, BigDecimal.ZERO, true)
                )
        );

        List<CarDashboardViewServiceModel> actual = carService.getAllCarDashboardServiceViewModelsByUser(USER_ID);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetCarInsuranceAddSelectViewReturnsAllUserCars() {
        List<Car> cars = createCars();

        Mockito
                .when(carRepository.findAllByUserId(USER_ID))
                .thenReturn(cars);

        List<CarInsuranceAddSelectView> expected = cars
                .stream()
                .map(car -> new CarInsuranceAddSelectView(
                        car.getId(),
                        car.getModel().getBrandName() + " " + car.getModel().getModelName())
                )
                .toList();

        List<CarInsuranceAddSelectView> actual = carService.getCarInsuranceAddSelectView(USER_ID);

        assertEquals(expected, actual);
    }

    private List<Car> createCars() {
        return List.of(
                car,
                new Car(UUID.randomUUID(), new CarModel(null, "some brand", "some model", null), engine, CAR_VIN, CAR_YEAR, CAR_CATEGORY, user, null, null, new ArrayList<>()),
                new Car(UUID.randomUUID(), new CarModel(null, "some brand 2", "some model 2", null), null, CAR_VIN, CAR_YEAR, CAR_CATEGORY, user, CAR_REGISTRATION, null, null),
                new Car(UUID.randomUUID(), new CarModel(null, "some brand 1", "some model 1", null), engine, null, CAR_YEAR, CAR_CATEGORY, user, null, new ArrayList<>(), null)
        );
    }

}
