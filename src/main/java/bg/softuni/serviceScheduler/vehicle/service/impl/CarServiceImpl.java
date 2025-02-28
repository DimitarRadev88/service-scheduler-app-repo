package bg.softuni.serviceScheduler.vehicle.service.impl;

import bg.softuni.serviceScheduler.engine.model.Engine;
import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.service.UserService;
import bg.softuni.serviceScheduler.vehicle.dao.CarBrandRepository;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.model.CarModel;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.vignette.model.Vignette;
import bg.softuni.serviceScheduler.web.dto.VehicleAddBindingModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final CarBrandRepository carBrandRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, UserRepository userRepository, CarBrandRepository carBrandRepository) {
        this.carRepository = carRepository;
        this.userRepository = userRepository;
        this.carBrandRepository = carBrandRepository;
    }


    @Override
    @Transactional
    public List<CarDashboardViewServiceModel> getCarDashboardServiceModels() {
        List<Car> all = carRepository.findAll();

        return all.stream().map(car -> new CarDashboardViewServiceModel(
                car.getModel().getBrand().getName(),
                car.getModel().getName(),
                car.getVin(),
                getOilChangesCost(car)
                        .add(getVignettesCost(car))
                        .add(getInsurancesCost(car)),
                car.getEngine().getMileage() > car.getEngine().getOilChanges().getLast().getMileage() + car.getEngine().getOilChanges().getLast().getChangeInterval() ||
                (car.getInsurances().getLast().getEndDate()).isAfter(LocalDate.now()) ||
                (car.getVignettes().getLast().getStartDate().isAfter(car.getVignettes().getLast().getEndDate()))
        )).toList();
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
    public List<CarServicesDoneViewServiceModel> getAllServices() {
        List<Car> all = carRepository.findAll();
        List<CarServicesDoneViewServiceModel> services = all
                .stream()
                .flatMap(car -> car
                        .getEngine()
                        .getOilChanges()
                        .stream()
                )
                .map(oilChange ->
                        new CarServicesDoneViewServiceModel(
                                "Oil change",
                                oilChange.getChangeDate(),
                                oilChange.getCost())
                )
                .collect(Collectors.toList());


        List<CarServicesDoneViewServiceModel> insurances = all
                .stream()
                .flatMap(car -> car
                        .getInsurances()
                        .stream()
                )
                .map(insurance ->
                        new CarServicesDoneViewServiceModel(
                                "Insurance",
                                insurance.getAddedAt(),
                                insurance.getCost())
                )
                .toList();

        List<CarServicesDoneViewServiceModel> vignettes = all
                .stream()
                .flatMap(car -> car
                        .getVignettes()
                        .stream()
                )
                .map(vignette ->
                        new CarServicesDoneViewServiceModel(
                                "Insurance",
                                vignette.getAddedAt(),
                                vignette.getCost())
                )
                .toList();

        services.addAll(insurances);
        services.addAll(vignettes);

        return services
                .stream()
                .sorted(Comparator.comparing(CarServicesDoneViewServiceModel::date))
                .toList();

    }

    @Override
    public void doAdd(VehicleAddBindingModel vehicleAdd, UUID userId) {
         User user = userRepository.findById(userId).orElseThrow(() ->  new RuntimeException("No active user found"));

        Car car = new Car();

        car.setUser(user);
        Engine engine = new Engine();
        engine.setDisplacement(vehicleAdd.displacement());
        engine.setMileage(vehicleAdd.mileage());
        engine.setOilCapacity(vehicleAdd.oilCapacity());
        engine.setFuelType(vehicleAdd.fuelType());

        car.setEngine(engine);
        car.setCategory(vehicleAdd.category());
//        car.setModel(vehicleAdd.model());

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
}
