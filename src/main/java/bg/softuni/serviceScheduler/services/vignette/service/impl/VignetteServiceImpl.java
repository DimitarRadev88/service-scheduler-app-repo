package bg.softuni.serviceScheduler.services.vignette.service.impl;


import bg.softuni.serviceScheduler.services.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.services.vignette.model.Vignette;
import bg.softuni.serviceScheduler.services.vignette.model.VignetteCost;
import bg.softuni.serviceScheduler.services.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.service.CarService;
import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class VignetteServiceImpl implements VignetteService {

    private final VignetteRepository vignetteRepository;
    private final CarRepository carRepository;

    @Autowired
    public VignetteServiceImpl(VignetteRepository vignetteRepository, CarRepository carRepository) {
        this.vignetteRepository = vignetteRepository;
        this.carRepository = carRepository;
    }

    @Override
    @Transactional
    public void doAdd(VignetteAddBindingModel vignetteAdd, UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("car not found"));

        LocalDate endDate = vignetteAdd.fromDate().plusDays(vignetteAdd.validity().getDays());
        Vignette vignette = new Vignette(
                null,
                LocalDate.now(),
                VignetteCost.valueOf(vignetteAdd.validity().name()).getCost(),
                vignetteAdd.fromDate(),
                endDate,
                vignetteAdd.validity(),
                endDate.isAfter(LocalDate.now()),
                car
        );
        vignetteRepository.save(vignette);
        car.getVignettes()
                .add(vignette);
        carRepository.save(car);

        log.info("Vignette expiring on {} added for {}", vignette.getEndDate(), car.getModel().getModelName());
    }

    @Override
    public boolean hasActiveVignette(UUID id) {
        return vignetteRepository.existsByCarIdAndIsValidTrue(id);
    }

    @Override
    public BigDecimal getSumVignetteCostByUserId(UUID userId) {
        BigDecimal sum = vignetteRepository.getSumVignetteCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void changeAllExpiredVignettesIsValidStatus() {
        List<Vignette> all = vignetteRepository.findAllByIsValidIsTrueAndEndDateIsBefore(LocalDate.now());

        all.forEach(vignette -> {vignette.setIsValid(false);});

        vignetteRepository.saveAll(all);
    }

}
