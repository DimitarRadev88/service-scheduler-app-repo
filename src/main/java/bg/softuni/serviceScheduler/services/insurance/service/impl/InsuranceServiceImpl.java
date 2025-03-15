package bg.softuni.serviceScheduler.services.insurance.service.impl;

import bg.softuni.serviceScheduler.services.insurance.dao.InsuranceRepository;
import bg.softuni.serviceScheduler.services.insurance.model.Insurance;
import bg.softuni.serviceScheduler.services.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
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
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final CarRepository carRepository;

    @Autowired
    public InsuranceServiceImpl(InsuranceRepository insuranceRepository, CarRepository carRepository) {
        this.insuranceRepository = insuranceRepository;
        this.carRepository = carRepository;
    }

    @Override
    public void doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new RuntimeException("Car not found"));

        Insurance save = insuranceRepository.save(new Insurance(
                null,
                LocalDate.now(),
                insuranceAdd.cost(),
                insuranceAdd.companyName(),
                insuranceAdd.fromDate(),
                insuranceAdd.fromDate().plusDays(insuranceAdd.insuranceValidityPeriod().getDays()),
                insuranceAdd.insuranceValidityPeriod(),
                insuranceAdd.fromDate().plusDays(insuranceAdd.insuranceValidityPeriod().getDays()).isAfter(LocalDate.now()),
                car
        ));

        log.info("Insurance expiring on {} for {} added", save.getEndDate(), car.getModel().getModelName());
    }

    @Override
    public Boolean hasActiveInsurance(UUID carId) {
        return insuranceRepository.existsByIsValidTrueAndCarId(carId);
    }

    @Override
    public BigDecimal getSumInsuranceCostByUserId(UUID userId) {
        BigDecimal sum = insuranceRepository.getSumInsuranceCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void changeAllExpiredInsurancesIsValidStatus() {
        List<Insurance> all = insuranceRepository.findAllByIsValidIsTrueAndEndDateIsBefore(LocalDate.now());

        all.forEach(insurance -> {insurance.setIsValid(false);});

        insuranceRepository.saveAll(all);
    }

}
