package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.insurance.model.InsuranceValidity;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record InsuranceAddBindingModel(
        @Size(min = 2, max = 30, message = "Company name must be between 3 and 30 characters")
        String companyName,
        @NotNull(message = "You must select insurance start date")
        LocalDate startDate,
        @NotNull(message = "You must select insurance period")
        InsuranceValidity insuranceValidity,
        @PositiveOrZero(message = "Insurance cost cannot be less than zero")
        BigDecimal price
) {
}
