package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CarDashboardViewServiceModel(
        UUID id,
        String make,
        String model,
        String vin,
        BigDecimal spent,
        Boolean needsService
) {
}
