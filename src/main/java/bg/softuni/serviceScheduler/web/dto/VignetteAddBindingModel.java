package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.services.vignette.model.VignetteValidity;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VignetteAddBindingModel(
        @NotNull(message = "You must select vignette start date")
        LocalDate fromDate,
        @NotNull(message = "You must select vignette validity period")
        VignetteValidity validity
) {
}
