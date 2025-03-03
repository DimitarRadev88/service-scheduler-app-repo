package bg.softuni.serviceScheduler.vehicle.service.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record EngineMileageAddBindingModel(
        @PositiveOrZero(message = "Engine mileage cannot be negative")
        Integer mileage
) {
}
