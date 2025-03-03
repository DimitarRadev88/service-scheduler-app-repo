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
    public List<CarDashboardServicesDoneViewServiceModel> getAllServices() {
        List<Car> all = carRepository.findAll();
        List<CarDashboardServicesDoneViewServiceModel> services = all
                .stream()
                .flatMap(car -> car
                        .getEngine()
                        .getOilChanges()
                        .stream()
                )
                .map(oilChange ->
                        new CarDashboardServicesDoneViewServiceModel(
                                "Oil change",
                                oilChange.getChangeDate(),
                                oilChange.getCost())
                )
                .collect(Collectors.toList());


        List<CarDashboardServicesDoneViewServiceModel> insurances = all
                .stream()
                .flatMap(car -> car
                        .getInsurances()
                        .stream()
                )
                .map(insurance ->
                        new CarDashboardServicesDoneViewServiceModel(
                                "Insurance",
                                insurance.getAddedAt(),
                                insurance.getCost())
                )
                .toList();

        List<CarDashboardServicesDoneViewServiceModel> vignettes = all
                .stream()
                .flatMap(car -> car
                        .getVignettes()
                        .stream()
                )
                .map(vignette ->
                        new CarDashboardServicesDoneViewServiceModel(
                                "Insurance",
                                vignette.getAddedAt(),
                                vignette.getCost())
                )
                .toList();

        services.addAll(insurances);
        services.addAll(vignettes);

        return services
                .stream()
                .sorted(Comparator.comparing(CarDashboardServicesDoneViewServiceModel::date))
                .toList();

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

        List<OilChange> oilChanges = car.getEngine().getOilChanges().stream().sorted(Comparator.comparingInt(OilChange::getMileage).reversed()).toList();
        return new CarInfoServiceViewModel(
                car.getId(),
                car.getModel().getBrand().getName() + " " + car.getModel().getName(),
                oilChanges.isEmpty() ? 100 : oilChanges.getFirst().getMileage() - car.getEngine().getMileage() >= 0 ? 0 : Math.min((car.getEngine().getMileage() - oilChanges.getFirst().getMileage()) * 100 / oilChanges.getFirst().getChangeInterval(), 100),
                new LastServicesServiceViewModel(
                        oilChanges.isEmpty() ?
                                new CarOilChangeDateAndIdServiceViewModel(null, null, true, true) :
                                new CarOilChangeDateAndIdServiceViewModel(
                                        oilChanges.getFirst().getId(),
                                        oilChanges.getFirst().getChangeDate(),
                                        car.getEngine().getMileage() + 1000 >= oilChanges.getFirst().getMileage() + oilChanges.getFirst().getChangeInterval(),
                                        car.getEngine().getMileage() >= oilChanges.getFirst().getMileage() + oilChanges.getFirst().getChangeInterval()
                                ),
                        car.getInsurances().isEmpty() ?
                                new InsurancePaymentDateAndIdServiceViewModel(null, null, true, true) :
                                new InsurancePaymentDateAndIdServiceViewModel(
                                        car.getInsurances().getLast().getId(),
                                        car.getInsurances().getLast().getAddedAt(),
                                        car.getInsurances().getLast().getEndDate().isAfter(LocalDate.now().plusWeeks(1)),
                                        car.getInsurances().getLast().getEndDate().isAfter(LocalDate.now())
                                ),
                        car.getVignettes().isEmpty() ?
                                new VignetteDateAndIdServiceViewModel(null, null, true, true) :
                                new VignetteDateAndIdServiceViewModel(
                                        car.getVignettes().getLast().getId(),
                                        car.getVignettes().getLast().getAddedAt(),
                                        car.getVignettes().getLast().getEndDate().isAfter(LocalDate.now().plusDays(1)),
                                        car.getVignettes().getLast().getEndDate().isAfter(LocalDate.now())
                                )
                ),
                new CarInfoEngineViewModel(
                        car.getEngine().getId(),
                        car.getEngine().getMileage()
                )
        );
    }

    @Override
    public EngineOilChangeServiceViewModel getEngineOilChangeAddViewModel(UUID engineId) {
        Engine engine = engineRepository.findById(engineId).orElseThrow(() -> new RuntimeException("Engine not found"));

        Car car = engine.getCar();

        return new EngineOilChangeServiceViewModel(
                engine.getId(),
                engine.getFuelType().name().charAt(0) + engine.getFuelType().name().substring(1).toLowerCase(),
                engine.getDisplacement(),
                engine.getOilCapacity(),
                engine.getOilFilter(),
                engine.getMileage(),
                new CarOilChangeAddServiceViewModel(
                        car.getId(),
                        car.getModel().getBrand().getName() + " " + car.getModel().getName(),

                        car.getVin()
                )
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

}
