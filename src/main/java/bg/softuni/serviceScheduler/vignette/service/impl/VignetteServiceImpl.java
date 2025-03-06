package bg.softuni.serviceScheduler.vignette.service.impl;

import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.vignette.model.Vignette;
import bg.softuni.serviceScheduler.vignette.model.VignetteCost;
import bg.softuni.serviceScheduler.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Service
public class VignetteServiceImpl implements VignetteService {

    private final VignetteRepository vignetteRepository;
    private final CarService carService;
    private final CarRepository carRepository;

    @Autowired
    public VignetteServiceImpl(VignetteRepository vignetteRepository, CarService carService, CarRepository carRepository) {
        this.vignetteRepository = vignetteRepository;
        this.carService = carService;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional
    public void doAdd(VignetteAddBindingModel vignetteAdd, UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("car not found"));

        LocalDate endDate = vignetteAdd.fromDate().plusDays(vignetteAdd.vignetteValidityPeriod().getDays());
        Vignette vignette = new Vignette(
                null,
                vignetteAdd.fromDate(),
                endDate,
                vignetteAdd.vignetteValidityPeriod(),
                VignetteCost.valueOf(vignetteAdd.vignetteValidityPeriod().name()).getCost(),
                endDate.isAfter(LocalDate.now()),
                car,
                LocalDate.now()
        );
        vignetteRepository.save(vignette);
        car.getVignettes()
                .add(vignette);
        carRepository.save(car);

        log.info("Vignette expiring on {} added for {}", vignette.getEndDate(), car.getModel().getName());
    }
}
