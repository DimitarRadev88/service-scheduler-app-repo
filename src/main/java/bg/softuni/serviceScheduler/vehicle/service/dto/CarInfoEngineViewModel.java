package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.util.UUID;

public record CarInfoEngineViewModel(
        UUID id,
        Integer mileage
) {
}
