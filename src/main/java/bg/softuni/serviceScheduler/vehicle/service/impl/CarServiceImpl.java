package bg.softuni.serviceScheduler.vehicle.service.impl;

import bg.softuni.serviceScheduler.carModel.dao.CarModelRepository;
import bg.softuni.serviceScheduler.carModel.model.CarModel;
import bg.softuni.serviceScheduler.carServices.insurance.model.Insurance;
import bg.softuni.serviceScheduler.carServices.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.carServices.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.carServices.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.carServices.vignette.model.Vignette;
import bg.softuni.serviceScheduler.carServices.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.carServices.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.dto.CarServiceAddSelectView;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.dao.EngineRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.exception.EngineNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarModelRepository carModelRepository;
    private final OilChangeRepository oilChangeRepository;
    private final EngineRepository engineRepository;
    private final InsuranceService insuranceService;
    private final VignetteService vignetteService;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository, CarModelRepository carModelRepository, OilChangeRepository oilChangeRepository, EngineRepository engineRepository, InsuranceService insuranceService, VignetteService vignetteService) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carModelRepository = carModelRepository;
        this.oilChangeRepository = oilChangeRepository;
        this.engineRepository = engineRepository;
        this.insuranceService = insuranceService;
        this.vignetteService = vignetteService;
    }

    @Transactional
    @Override
    public List<CarDashboardServicesDoneViewServiceModel> getAllServicesByUser(UUID userId) {
        checkUser(userId);

        List<Car> all = carRepository.findAllByUserId(userId);

        return mapToCarDashboardServicesDoneViewServiceModel(all);
    }

    @Override
    @Transactional
    public UUID doAdd(CarAddBindingModel vehicleAdd, UUID userId) {
        User user = getUser(userId);

        Car car = map(vehicleAdd);

        car.setUser(user);

        Car saved = carRepository.save(car);
        Engine savedEngine = saved.getEngine();
        savedEngine.setCar(car);
        engineRepository.save(savedEngine);

        log.info("User {} added {} with engine {}", user.getId(), car.getModel().getModelName(), savedEngine.getId());

        return saved.getId();
    }

    @Override
    public CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id) {
        Car car = getCar(id);

        return mapToCarInsuranceAddServiceView(car);
    }

    private Car getCar(UUID id) {
        return carRepository.findById(id).orElseThrow(() -> new CarNotFoundException("Car not found"));
    }

    @Override
    @Transactional
    public CarInfoServiceViewModel getCarInfoServiceViewModel(UUID id) {
        Car car = getCar(id);

        return mapToCarInfoServiceViewModel(car);
    }

    @Override
    public EngineOilChangeServiceViewModel getEngineOilChangeAddViewModel(UUID engineId) {
        Engine engine = engineRepository.findById(engineId).orElseThrow(() -> new EngineNotFoundException("Engine not found"));

        return mapToEngineOilChangeServiceViewModel(engine);
    }

    @Override
    public void doAddMileage(EngineMileageAddBindingModel engineMileageAdd, UUID id) {
        Car car = getCar(id);

        car.getEngine().setMileage(engineMileageAdd.newMileage());

        carRepository.save(car);
    }

    @Override
    public void doDelete(UUID id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException("Car not found");
        }

        carRepository.deleteById(id);
    }

    @Override
    public CarVignetteAddServiceView getCarVignetteAddServiceView(UUID id) {
        return carRepository.findById(id)
                .map(CarServiceImpl::mapToCarVignetteAdServiceView
                )
                .orElseThrow(() -> new CarNotFoundException("Car not found"));
    }

    @Override
    public BigDecimal getAllServicesCostByUser(UUID userId) {
        checkUser(userId);

        return insuranceService.getSumInsuranceCostByUserId(userId)
                .add(vignetteService.getSumVignetteCostByUserId(userId))
                .add(getSumOilChangesCostByUser(userId));
    }

    @Override
    @Transactional
    public List<CarDashboardViewServiceModel> getAllCarDashboardServiceViewModelsByUser(UUID id) {
        checkUser(id);

        return carRepository.findAllByUserId(id).stream().map(this::mapToCarDashboardViewServiceModel).toList();
    }

    @Override
    @Transactional
    public List<CarServiceAddSelectView> getCarInsuranceAddSelectView(UUID id) {
        return carRepository.findAllByUserId(id)
                .stream()
                .map(CarServiceImpl::maptoCarInsuranceAddSelectView)
                .toList();
    }

    private static CarServiceAddSelectView maptoCarInsuranceAddSelectView(Car car) {
        return new CarServiceAddSelectView(
                car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName()
        );
    }


    private CarDashboardViewServiceModel mapToCarDashboardViewServiceModel(Car car) {
        BigDecimal oilChangesSum = oilChangeRepository.getSumOilChangesCostByEngineId(car.getEngine().getId());
        return new CarDashboardViewServiceModel(car.getId(),
                car.getModel().getBrandName(),
                car.getModel().getModelName(),
                car.getVin(),
                oilChangesSum == null ? BigDecimal.ZERO : oilChangesSum
                        .add(insuranceService.getSumInsuranceCostByCarId(car.getId()))
                        .add(vignetteService.getSumVignetteCostByCarId(car.getId())),
                !insuranceService.hasActiveInsurance(car.getId())
                || needsOilChange(car.getEngine())
                || !vignetteService.hasActiveVignette(car.getId())
        );
    }

    private BigDecimal getSumOilChangesCostByUser(UUID userId) {
        BigDecimal sum = oilChangeRepository.getSumOilChangesCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    private static CarOilChangeAddServiceViewModel mapToCarOilChangeAddServiceViewModel(Car car) {
        return new CarOilChangeAddServiceViewModel(
                car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                car.getVin()
        );
    }

    private static List<CarDashboardServicesDoneViewServiceModel> mapToCarDashboardServicesDoneViewServiceModel(List<Car> all) {
        return Stream.concat(
                        getOilChangesServiceView(all),
                        Stream.concat(
                                getInsurancesServiceView(all),
                                getVignettesServiceView(all)))
                .sorted(Comparator.comparing(CarDashboardServicesDoneViewServiceModel::date)
                        .reversed())
                .toList();
    }

    private static Stream<CarDashboardServicesDoneViewServiceModel> getVignettesServiceView(List<Car> all) {
        return all.stream()
                .map(Car::getVignettes)
                .flatMap(List::stream)
                .map(vignette -> new CarDashboardServicesDoneViewServiceModel("Vignette", vignette.getAddedAt(), vignette.getCost()));
    }

    private static Stream<CarDashboardServicesDoneViewServiceModel> getInsurancesServiceView(List<Car> all) {
        return all.stream()
                .map(Car::getInsurances)
                .flatMap(List::stream)
                .map(insurance -> new CarDashboardServicesDoneViewServiceModel("Insurance", insurance.getAddedAt(), insurance.getCost()));
    }

    private static Stream<CarDashboardServicesDoneViewServiceModel> getOilChangesServiceView(List<Car> all) {
        return all.stream()
                .map(car -> car.getEngine().getOilChanges())
                .flatMap(List::stream)
                .map(oilChange -> new CarDashboardServicesDoneViewServiceModel("Oil change", oilChange.getAddedAt(), oilChange.getCost()));
    }

    private CarInfoServiceViewModel mapToCarInfoServiceViewModel(Car car) {

        Optional<OilChange> optionalOilChange = oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(car.getEngine().getId());

        return new CarInfoServiceViewModel(
                car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                car.getVin(),
                car.getRegistration(),
                getLastServices(car),
                mapToCarInfoEngineViewModel(car, optionalOilChange)
        );
    }

    private static CarInfoEngineViewModel mapToCarInfoEngineViewModel(Car car, Optional<OilChange> optionalOilChange) {
        return new CarInfoEngineViewModel(
                car.getEngine().getId(),
                car.getEngine().getFuelType(),
                car.getEngine().getDisplacement(),
                car.getEngine().getMileage(),
                getOilChangeLifePercent(optionalOilChange, car.getEngine().getMileage()),
                optionalOilChange.map(OilChange::getChangeInterval).orElse(0),
                optionalOilChange.map(OilChange::getMileage).orElse(0)
        );
    }

    private static Integer getOilChangeLifePercent(Optional<OilChange> optionalOilChange, Integer mileage) {
        return optionalOilChange.map(oilChange -> oilChange.getMileage() - mileage >= 0
                        ? 0
                        : Math.min((mileage - oilChange.getMileage()) * 100 / oilChange.getChangeInterval(), 100))
                .orElse(100);
    }

    private LastServicesServiceViewModel getLastServices(Car car) {
        return new LastServicesServiceViewModel(
                getLastCarOilChangeDateAndIdServiceViewModel(car),
                getLastInsurancePaymentDateAndIdServiceVieModel(car),
                getLastVignetteDateAndIdServiceViewModel(car)
        );
    }

    private static VignetteDateAndIdServiceViewModel getLastVignetteDateAndIdServiceViewModel(Car car) {
        if (car.getVignettes().isEmpty()) {
            return new VignetteDateAndIdServiceViewModel(null, null, true, true);
        }

        Vignette vignette = car.getVignettes().getLast();

        return new VignetteDateAndIdServiceViewModel(
                vignette.getId(),
                vignette.getAddedAt(),
                vignette.getEndDate().isBefore(LocalDate.now().plusDays(1)),
                vignette.getEndDate().isBefore(LocalDate.now())
        );
    }

    private static InsurancePaymentDateAndIdServiceViewModel getLastInsurancePaymentDateAndIdServiceVieModel(Car car) {
        if (car.getInsurances().isEmpty()) {
            return new InsurancePaymentDateAndIdServiceViewModel(null, null, true, true);
        }

        Insurance insurance = car.getInsurances().getLast();

        return new InsurancePaymentDateAndIdServiceViewModel(
                insurance.getId(),
                insurance.getAddedAt(),
                insurance.getEndDate().isBefore(LocalDate.now().plusWeeks(1)),
                insurance.getEndDate().isBefore(LocalDate.now())
        );
    }

    private static CarOilChangeDateAndIdServiceViewModel getLastCarOilChangeDateAndIdServiceViewModel(Car car) {
        List<OilChange> oilChanges = car.getEngine().getOilChanges();

        if (oilChanges.isEmpty()) {
            return new CarOilChangeDateAndIdServiceViewModel(null, null);
        }

        OilChange oilChange = oilChanges.getFirst();

        return new CarOilChangeDateAndIdServiceViewModel(
                oilChange.getId(),
                oilChange.getChangeDate()
        );
    }

    private Car map(CarAddBindingModel vehicleAdd) {
        Car car = new Car();
        Optional<CarModel> optionalModel = carModelRepository.findCarModelByBrandNameAndModelName(vehicleAdd.brand(), vehicleAdd.model());
        CarModel carModel = optionalModel.orElse(null);
        if (carModel == null) {
            carModel = new CarModel();
            carModel.setBrandName(vehicleAdd.brand());
            carModel.setModelName(vehicleAdd.model());
        }

        car.setModel(carModel);

        Engine engine = new Engine();
        engine.setDisplacement(vehicleAdd.displacement());
        engine.setMileage(vehicleAdd.mileage());
        engine.setOilCapacity(vehicleAdd.oilCapacity());
        engine.setFuelType(vehicleAdd.fuelType());

        car.setEngine(engine);

        car.setCategory(vehicleAdd.category());
        car.setVin(vehicleAdd.vin());
        car.setYearOfProduction(vehicleAdd.year());
        car.setRegistration(vehicleAdd.registration());

        return car;
    }

    private static CarInsuranceAddServiceView mapToCarInsuranceAddServiceView(Car car) {
        return new CarInsuranceAddServiceView(car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                car.getEngine().getFuelType(),
                car.getEngine().getDisplacement(),
                car.getVin(),
                car.getRegistration());
    }

    private boolean needsOilChange(Engine engine) {
        return oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(engine.getId())
                .map(oilChange -> oilChange.getMileage() + oilChange.getChangeInterval() <= engine.getMileage())
                .orElse(true);
    }

    private static EngineOilChangeServiceViewModel mapToEngineOilChangeServiceViewModel(Engine engine) {
        Car car = engine.getCar();
        return new EngineOilChangeServiceViewModel(
                engine.getId(),
                engine.getFuelType().name().charAt(0) + engine.getFuelType().name().substring(1).toLowerCase(),
                engine.getDisplacement(),
                engine.getOilCapacity(),
                engine.getOilFilter(),
                engine.getMileage(),
                mapToCarOilChangeAddServiceViewModel(car)
        );
    }

    private static CarVignetteAddServiceView mapToCarVignetteAdServiceView(Car car) {
        return new CarVignetteAddServiceView(
                car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                car.getRegistration());
    }

    private void checkUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
    }

    private User getUser(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
