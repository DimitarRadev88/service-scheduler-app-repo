package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OilChangeAddBindingModel(
        @NotNull(message = "Please enter the date of changing")
        LocalDate changeDate,
        @NotNull(message = "Please enter engine mileage when change was done")
        @Positive(message = "Mileage cannot be less than '0'")
        Integer changeMileage,
        @NotNull(message = "Please enter oil change interval")
        @Positive(message = "Change interval must be more than '0'")
        Integer changeInterval,
        @NotNull(message = "Please enter change cost")
        @Positive(message = "Change cost must be more than '0")
        BigDecimal cost
) {
}
