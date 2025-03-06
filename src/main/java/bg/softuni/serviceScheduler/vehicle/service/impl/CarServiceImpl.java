package bg.softuni.serviceScheduler.vehicle.service.impl;


import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.vehicle.dao.*;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.CarModel;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.vehicle.model.OilChange;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.vignette.model.Vignette;
import bg.softuni.serviceScheduler.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarBrandRepository carBrandRepository;
    private final CarModelRepository carModelRepository;
    private final OilChangeRepository oilChangeRepository;
    private final EngineRepository engineRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository, CarBrandRepository carBrandRepository, CarModelRepository carModelRepository, OilChangeRepository oilChangeRepository, EngineRepository engineRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carBrandRepository = carBrandRepository;
        this.carModelRepository = carModelRepository;
        this.oilChangeRepository = oilChangeRepository;
        this.engineRepository = engineRepository;
    }

    private static BigDecimal getOilChangesCost(Car car) {
        return car
                .getEngine()
                .getOilChanges()
                .stream()
                .map(OilChange::getCost)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal getVignettesCost(Car car) {
        return car.getVignettes()
                .stream()
                .map(Vignette::getCost).
                reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal getInsurancesCost(Car car) {
        return car.getInsurances()
                .stream()
                .map(Insurance::getCost)
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Transactional
    @Override
    public List<CarDashboardServicesDoneViewServiceModel> getAllServicesByUser(UUID userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Car> all = carRepository.findAllByUserId(userId);

        return Stream.concat(
                all.stream()
                        .map(car -> car.getEngine().getOilChanges())
                        .flatMap(List::stream)
                        .map(oilChange -> new CarDashboardServicesDoneViewServiceModel("Oil change", oilChange.getDate(), oilChange.getCost())),
                Stream.concat(
                        all.stream()
                                .map(Car::getInsurances)
                                .flatMap(List::stream)
                                .map(insurance -> new CarDashboardServicesDoneViewServiceModel("Insurance", insurance.getAddedAt(), insurance.getCost())),
                        all.stream()
                                .map(Car::getVignettes)
                                .flatMap(List::stream)
                                .map(vignette -> new CarDashboardServicesDoneViewServiceModel("Vignette", vignette.getAddedAt(), vignette.getCost())))).toList();

    }

    @Override
    @Transactional
    public UUID doAdd(CarAddBindingModel vehicleAdd, UUID userId) {
        Car car = new Car();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No active user found"));
        car.setUser(user);

        Optional<CarModel> carModel = carModelRepository.findCarModelByBrandNameAndName(vehicleAdd.make(), vehicleAdd.model());
        if (carModel.isEmpty()) {
            throw new RuntimeException("Unknown car Make or Model!");
        }

        car.setModel(carModel.get());

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

        Car save = carRepository.save(car);
        Engine savedEngine = save.getEngine();
        savedEngine.setCar(car);
        engineRepository.save(savedEngine);

        log.info("User {} added {} with engine {}", user.getId(), car.getModel().getName(), savedEngine.getId());

        return save.getId();
    }

    @Override
    @Transactional
    public Map<String, List<String>> getAllBrandsWithModels() {
        Map<String, List<String>> models = new LinkedHashMap<>();

        carBrandRepository.findAll().forEach(carBrand -> {
            models.put(carBrand.getName(), carBrand.getModels().stream().map(CarModel::getName).collect(Collectors.toList()));
        });

        return models;
    }

    @Override
    public CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));

        return new CarInsuranceAddServiceView(car.getId(),
                car.getModel().getBrand().getName() + " " + car.getModel().getName(),
                car.getEngine().getFuelType(),
                car.getEngine().getDisplacement(),
                car.getVin(),
                car.getRegistration());
    }

    @Override
    @Transactional
    public CarInfoServiceViewModel getCarInfoServiceViewModel(UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));


        return mapToCarInfoServiceViewModel(car);
    }

    private CarInfoServiceViewModel mapToCarInfoServiceViewModel(Car car) {

        Optional<OilChange> optionalOilChange = oilChangeRepository.findFirstByEngineIdOrderByDateDesc(car.getEngine().getId());

        return new CarInfoServiceViewModel(
                car.getId(),
                car.getModel().getBrand().getName() + " " + car.getModel().getName(),
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
                optionalOilChange.map(oilChange -> oilChange.getMileage() - car.getEngine().getMileage() >= 0
                                ? 0
                                : Math.min((car.getEngine().getMileage() - oilChange.getMileage()) * 100 / oilChange.getChangeInterval(), 100))
                        .orElse(100),
                optionalOilChange.map(OilChange::getChangeInterval).orElse(0),
                optionalOilChange.map(OilChange::getMileage).orElse(0)
        );
    }

    @Override
    public LastServicesServiceViewModel getLastServices(Car car) {
        return new LastServicesServiceViewModel(
                getLastCarOilChangeDateAndIdServiceViewModel(car),
                getLasttInsurancePaymentDateAndIdServiceVieModel(car),
                getLastVignetteDateAndIdServiceViewModel(car)
        );
    }

    private static VignetteDateAndIdServiceViewModel getLastVignetteDateAndIdServiceViewModel(Car car) {
        return car.getVignettes().isEmpty() ?
                new VignetteDateAndIdServiceViewModel(null, null, true, true) :
                new VignetteDateAndIdServiceViewModel(
                        car.getVignettes().getLast().getId(),
                        car.getVignettes().getLast().getAddedAt(),
                        car.getVignettes().getLast().getEndDate().isAfter(LocalDate.now().plusDays(1)),
                        car.getVignettes().getLast().getEndDate().isAfter(LocalDate.now())
                );
    }

    private static InsurancePaymentDateAndIdServiceViewModel getLasttInsurancePaymentDateAndIdServiceVieModel(Car car) {
        return car.getInsurances().isEmpty() ?
                new InsurancePaymentDateAndIdServiceViewModel(null, null, true, true) :
                new InsurancePaymentDateAndIdServiceViewModel(
                        car.getInsurances().getLast().getId(),
                        car.getInsurances().getLast().getAddedAt(),
                        car.getInsurances().getLast().getEndDate().isAfter(LocalDate.now().plusWeeks(1)),
                        car.getInsurances().getLast().getEndDate().isAfter(LocalDate.now())
                );
    }

    private static CarOilChangeDateAndIdServiceViewModel getLastCarOilChangeDateAndIdServiceViewModel(Car car) {
        List<OilChange> oilChanges = car.getEngine().getOilChanges();
        return oilChanges.isEmpty() ?
                new CarOilChangeDateAndIdServiceViewModel(null, null) :
                new CarOilChangeDateAndIdServiceViewModel(
                        oilChanges.getFirst().getId(),
                        oilChanges.getFirst().getChangeDate()
                );
    }

    @Override
    public EngineOilChangeServiceViewModel getEngineOilChangeAddViewModel(UUID engineId) {
        Engine engine = engineRepository.findById(engineId).orElseThrow(() -> new RuntimeException("Engine not found"));

        return mapToEngineOilChangeServiceViewModel(engine);
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

    private static CarOilChangeAddServiceViewModel mapToCarOilChangeAddServiceViewModel(Car car) {
        return new CarOilChangeAddServiceViewModel(
                car.getId(),
                car.getModel().getBrand().getName() + " " + car.getModel().getName(),
                car.getVin()
        );
    }

    @Override
    @Transactional
    public UUID doAdd(OilChangeAddBindingModel oilChangeAdd, UUID engineId) {
        Engine engine = engineRepository.findById(engineId).orElseThrow(() -> new RuntimeException("Engine not found"));

        OilChange oilChange = new OilChange(
                null,
                engine,
                oilChangeAdd.cost(),
                LocalDate.now(),
                oilChangeAdd.changeMileage(),
                oilChangeAdd.changeInterval(),
                oilChangeAdd.changeDate()
        );

        OilChange save = oilChangeRepository.save(oilChange);
        engine.getOilChanges().add(save);

        engineRepository.save(engine);
        return engine.getCar().getId();
    }

    @Override
    public void doAddMileage(EngineMileageAddBindingModel engineMileageAdd, UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));

        car.getEngine().setMileage(engineMileageAdd.mileage());

        carRepository.save(car);
    }

    @Override
    public Long getOilChangesCount() {
        return oilChangeRepository.count();
    }

    @Override
    public boolean needsOilChange(UUID id) {
        return oilChangeRepository.findFirstByEngineIdOrderByDateDesc(id)
                .map(oilChange -> oilChange.getMileage() + oilChange.getChangeInterval() >= engineRepository.findByCarId(id)
                        .orElseThrow(() -> new RuntimeException("Car not found"))
                        .getMileage())
                .orElse(false);
    }

    @Override
    public void doDelete(UUID id) {
        if (!carRepository.existsById(id)) {
            throw new RuntimeException("Car not found");
        }

        carRepository.deleteById(id);
    }

    @Override
    public CarVignetteAddServiceView getCarVignetteAddServiceView(UUID id) {
        return carRepository.findById(id)
                .map(car -> new CarVignetteAddServiceView(
                        car.getId(),
                        car.getModel().getBrand().getName() + " " + car.getModel().getName(),
                        car.getRegistration())
                )
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

}
