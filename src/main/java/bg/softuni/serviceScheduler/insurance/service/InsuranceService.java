package bg.softuni.serviceScheduler.insurance.service;

import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import jakarta.validation.Valid;

import javax.script.CompiledScript;
import java.util.UUID;

public interface InsuranceService {

    void doAdd(InsuranceAddBindingModel insuranceAdd, UUID carId);

    Boolean hasActiveInsurance(UUID carId);
}
