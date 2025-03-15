package bg.softuni.serviceScheduler.vehicle.service.impl;

import bg.softuni.serviceScheduler.carModels.dao.CarModelRepository;
import bg.softuni.serviceScheduler.carModels.model.CarModel;
import bg.softuni.serviceScheduler.services.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.services.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.services.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.dao.EngineRepository;
import bg.softuni.serviceScheduler.services.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.services.oilChange.model.OilChange;
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
        if (userRepository.findById(userId).isEmpty()) {
            throw new RuntimeException("User not found");
        }

        List<Car> all = carRepository.findAllByUserId(userId);

        return Stream.concat(
                        all.stream()
                                .map(car -> car.getEngine().getOilChanges())
                                .flatMap(List::stream)
                                .map(oilChange -> new CarDashboardServicesDoneViewServiceModel("Oil change", oilChange.getAddedAt(), oilChange.getCost())),
                        Stream.concat(
                                all.stream()
                                        .map(Car::getInsurances)
                                        .flatMap(List::stream)
                                        .map(insurance -> new CarDashboardServicesDoneViewServiceModel("Insurance", insurance.getAddedAt(), insurance.getCost())),
                                all.stream()
                                        .map(Car::getVignettes)
                                        .flatMap(List::stream)
                                        .map(vignette -> new CarDashboardServicesDoneViewServiceModel("Vignette", vignette.getAddedAt(), vignette.getCost()))))
                .sorted(Comparator.comparing(CarDashboardServicesDoneViewServiceModel::date)
                        .reversed())
                .toList();

    }

    @Override
    @Transactional
    public UUID doAdd(CarAddBindingModel vehicleAdd, UUID userId) {
        Car car = new Car();

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No active user found"));
        car.setUser(user);

        Optional<CarModel> optionalModel = carModelRepository.findCarModelByBrandNameAndModelName(vehicleAdd.model(), vehicleAdd.model());
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

        Car save = carRepository.save(car);
        Engine savedEngine = save.getEngine();
        savedEngine.setCar(car);
        engineRepository.save(savedEngine);

        log.info("User {} added {} with engine {}", user.getId(), car.getModel().getModelName(), savedEngine.getId());

        return save.getId();
    }

    @Override
    public CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));

        return new CarInsuranceAddServiceView(car.getId(),
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
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
                getLastInsurancePaymentDateAndIdServiceVieModel(car),
                getLastVignetteDateAndIdServiceViewModel(car)
        );
    }

    private static VignetteDateAndIdServiceViewModel getLastVignetteDateAndIdServiceViewModel(Car car) {
        return car.getVignettes().isEmpty() ?
                new VignetteDateAndIdServiceViewModel(null, null, true, true) :
                new VignetteDateAndIdServiceViewModel(
                        car.getVignettes().getLast().getId(),
                        car.getVignettes().getLast().getAddedAt(),
                        car.getVignettes().getLast().getEndDate().isBefore(LocalDate.now().plusDays(1)),
                        car.getVignettes().getLast().getEndDate().isBefore(LocalDate.now())
                );
    }

    private static InsurancePaymentDateAndIdServiceViewModel getLastInsurancePaymentDateAndIdServiceVieModel(Car car) {
        return car.getInsurances().isEmpty() ?
                new InsurancePaymentDateAndIdServiceViewModel(null, null, true, true) :
                new InsurancePaymentDateAndIdServiceViewModel(
                        car.getInsurances().getLast().getId(),
                        car.getInsurances().getLast().getAddedAt(),
                        car.getInsurances().getLast().getEndDate().isBefore(LocalDate.now().plusWeeks(1)),
                        car.getInsurances().getLast().getEndDate().isBefore(LocalDate.now())
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
                car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                car.getVin()
        );
    }

    @Override
    public void doAddMileage(EngineMileageAddBindingModel engineMileageAdd, UUID id) {
        Car car = carRepository.findById(id).orElseThrow(() -> new RuntimeException("Car not found"));

        car.getEngine().setMileage(engineMileageAdd.newMileage());

        carRepository.save(car);
    }

    @Override
    public boolean needsOilChange(Engine engine) {
        return oilChangeRepository.findFirstByEngineIdOrderByMileageDesc(engine.getId())
                .map(oilChange -> oilChange.getMileage() + oilChange.getChangeInterval() <= engine.getMileage())
                .orElse(true);
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
                        car.getModel().getBrandName() + " " + car.getModel().getModelName(),
                        car.getRegistration())
                )
                .orElseThrow(() -> new RuntimeException("Car not found"));
    }

    @Override
    public BigDecimal getAllServicesCostByUser(UUID userId) {
        return insuranceService.getSumInsuranceCostByUserId(userId)
                .add(vignetteService.getSumVignetteCostByUserId(userId))
                .add(getSumOilChangesCostByUser(userId));
    }

    @Override
    public BigDecimal getSumOilChangesCostByUser(UUID userId) {
        BigDecimal sum = oilChangeRepository.getSumOilChangesCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

}
