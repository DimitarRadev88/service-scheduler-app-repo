package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.services.insurance.model.InsuranceValidity;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceAddBindingModel(
        @Size(min = 2, max = 30, message = "Company name must be between 3 and 30 characters")
        String companyName,
        @NotNull(message = "You must select insurance start date")
        LocalDate fromDate,
        @NotNull(message = "You must select insurance validity period")
        InsuranceValidity insuranceValidityPeriod,
        @Positive(message = "Insurance cost must be more than zero")
        BigDecimal cost
) {
}
