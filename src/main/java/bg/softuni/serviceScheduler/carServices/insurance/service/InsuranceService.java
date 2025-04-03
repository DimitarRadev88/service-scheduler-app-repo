package bg.softuni.serviceScheduler.carServices.insurance.service;

import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.UUID;

public interface InsuranceService {

    String doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId);

    Boolean hasActiveInsurance(UUID carId);

    BigDecimal getSumInsuranceCostByUserId(UUID userId);

    @Scheduled(cron = "0 0 0 * * *")
    void validateAllStartingInsurances();

    void invalidateAllExpiredInsurances();

    BigDecimal getSumInsuranceCostByCarId(UUID carId);
}
