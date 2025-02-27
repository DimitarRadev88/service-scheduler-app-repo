package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.math.BigDecimal;

public record CarDashboardViewServiceModel(
        String make,
        String model,
        String vin,
        BigDecimal spent,
        Boolean needsService
) {
}
