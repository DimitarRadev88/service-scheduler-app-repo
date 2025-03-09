package bg.softuni.serviceScheduler.insurance.service;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;

import java.util.UUID;

public interface InsuranceService {

    void doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId);

    Boolean hasActiveInsurance(UUID carId);
}
