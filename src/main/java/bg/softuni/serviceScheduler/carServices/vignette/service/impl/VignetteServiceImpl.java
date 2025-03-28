package bg.softuni.serviceScheduler.carServices.vignette.service.impl;


import bg.softuni.serviceScheduler.carServices.vignette.dao.VignetteRepository;
import bg.softuni.serviceScheduler.carServices.vignette.model.Vignette;
import bg.softuni.serviceScheduler.carServices.vignette.model.VignetteCost;
import bg.softuni.serviceScheduler.carServices.vignette.service.VignetteService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
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
    private final UserRepository userRepository;

    @Autowired
    public VignetteServiceImpl(VignetteRepository vignetteRepository, CarRepository carRepository, UserRepository userRepository) {
        this.vignetteRepository = vignetteRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void doAdd(VignetteAddBindingModel vignetteAdd, UUID carId) {
        Car car = carRepository
                .findById(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found"));

        LocalDate endDate = vignetteAdd.startDate().plusDays(vignetteAdd.validity().getDays());
        Vignette vignette = new Vignette(
                null,
                LocalDate.now(),
                VignetteCost.valueOf(vignetteAdd.validity().name()).getCost(),
                vignetteAdd.startDate(),
                endDate,
                vignetteAdd.validity(),
                endDate.isAfter(LocalDate.now()),
                car
        );

        Vignette saved = vignetteRepository.save(vignette);

        log.info("Vignette with id {} expiring on {} added for car with id {}",
                saved.getId(),
                saved.getEndDate(),
                car.getId()
        );
    }

    @Override
    public boolean hasActiveVignette(UUID id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException("Car not found");
        }

        return vignetteRepository.existsByCarIdAndIsValidTrue(id);
    }

    @Override
    public BigDecimal getSumVignetteCostByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        BigDecimal sum = vignetteRepository.getSumVignetteCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void invalidateAllExpiredVignettes() {
        List<Vignette> all = vignetteRepository.findAllByIsValidIsTrueAndEndDateIsBefore(LocalDate.now());

        all.forEach(vignette -> vignette.setIsValid(false));

        vignetteRepository.saveAll(all);

        log.info("All expired vignettes: {}", all);
    }

    @Override
    public BigDecimal getSumVignetteCostByCarId(UUID id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException("Car not found");
        }

        BigDecimal sum = vignetteRepository.getSumVignetteCostByCarId(id);
        return sum == null ? BigDecimal.ZERO : sum;
    }

}
