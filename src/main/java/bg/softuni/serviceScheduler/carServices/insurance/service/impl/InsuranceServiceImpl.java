package bg.softuni.serviceScheduler.carServices.insurance.service.impl;

import bg.softuni.serviceScheduler.carServices.insurance.dao.InsuranceRepository;
import bg.softuni.serviceScheduler.carServices.insurance.model.Insurance;
import bg.softuni.serviceScheduler.carServices.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.exception.UserNotFoundException;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.exception.CarNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
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
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;
    private final CarRepository carRepository;
    private final UserRepository userRepository;

    @Autowired
    public InsuranceServiceImpl(InsuranceRepository insuranceRepository, CarRepository carRepository, UserRepository userRepository) {
        this.insuranceRepository = insuranceRepository;
        this.carRepository = carRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new CarNotFoundException("Car not found"));

        Insurance saved = insuranceRepository.save(map(insuranceAdd, car));

        log.info("Insurance with id {} expiring on {} for car with id {} added", saved.getId(), saved.getEndDate(), car.getId());
    }

    @Override
    public Boolean hasActiveInsurance(UUID carId) {
        checkCar(carId);

        return insuranceRepository.existsByIsValidTrueAndCarId(carId);
    }

    @Override
    public BigDecimal getSumInsuranceCostByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }

        BigDecimal sum = insuranceRepository.getSumInsuranceCostByUserId(userId);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    @Override
    @Scheduled(cron = "0 0 0 * * *")
    public void invalidateAllExpiredInsurances() {
        List<Insurance> all = insuranceRepository.findAllByIsValidIsTrueAndEndDateIsBefore(LocalDate.now());

        all.forEach(insurance -> insurance.setIsValid(false));

        insuranceRepository.saveAll(all);
    }

    @Override
    public BigDecimal getSumInsuranceCostByCarId(UUID carId) {
        checkCar(carId);

        BigDecimal sumInsuranceCostByCarId = insuranceRepository.getSumInsuranceCostByCarId(carId);

        return sumInsuranceCostByCarId == null ? BigDecimal.ZERO : sumInsuranceCostByCarId;
    }

    private void checkCar(UUID carId) {
        if (!carRepository.existsById(carId)) {
            throw new CarNotFoundException("Car not found");
        }
    }

    private static Insurance map(InsuranceAddBindingModel insuranceAdd, Car car) {
        return new Insurance(
                null,
                LocalDate.now(),
                insuranceAdd.cost(),
                insuranceAdd.companyName(),
                insuranceAdd.startDate(),
                insuranceAdd.startDate().plusDays(insuranceAdd.insuranceValidityPeriod().getDays()),
                insuranceAdd.insuranceValidityPeriod(),
                insuranceAdd.startDate().plusDays(insuranceAdd.insuranceValidityPeriod().getDays()).isAfter(LocalDate.now()),
                car
        );
    }


}
