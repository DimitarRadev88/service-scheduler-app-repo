package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import bg.softuni.serviceScheduler.vignette.model.VignetteValidity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record VignetteAddBindingModel(
        @NotNull(message = "You must select vehicle category")
        VehicleCategory category,
        @NotNull(message = "You must select vignette start date")
        LocalDate fromDate,
        @NotNull(message = "You must select vignette validity period")
        VignetteValidity vignetteValidityPeriod
) {
}
