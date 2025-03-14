package bg.softuni.serviceScheduler.services.insurance.service;

import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface InsuranceService {

    void doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId);

    Boolean hasActiveInsurance(UUID carId);

    BigDecimal getSumInsuranceCostByUserId(UUID userId);

    void changeAllExpiredInsurancesIsValidStatus();

}
