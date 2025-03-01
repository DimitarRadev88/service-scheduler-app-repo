package bg.softuni.serviceScheduler.insurance.service.impl;

import bg.softuni.serviceScheduler.insurance.dao.InsuranceRepository;
import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.insurance.service.InsuranceService;
import bg.softuni.serviceScheduler.vehicle.dao.CarRepository;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                insuranceAdd.companyName(),
                insuranceAdd.startDate(),
                insuranceAdd.startDate().plusDays(insuranceAdd.insuranceValidity().getDays()),
                insuranceAdd.insuranceValidity(),
                insuranceAdd.price(),
                insuranceAdd.startDate().plusDays(insuranceAdd.insuranceValidity().getDays()).isAfter(LocalDate.now()),
                car,
                LocalDate.now()
        ));

        log.info("Insurance expiring on {} for {} added", save.getEndDate(), car.getModel().getName());
    }
}
