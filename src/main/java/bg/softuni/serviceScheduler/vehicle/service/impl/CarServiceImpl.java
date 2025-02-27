package bg.softuni.serviceScheduler.vehicle.service.impl;

import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
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
        //todo
    }
}
