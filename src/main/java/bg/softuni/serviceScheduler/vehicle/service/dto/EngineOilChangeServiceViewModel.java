package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.util.UUID;

public record EngineOilChangeServiceViewModel(
        UUID id,
        String fuelType,
        Integer displacement,
        Double oilCapacity,
        String oilFilter,
        Integer mileage,
        CarOilChangeAddServiceViewModel carView
) {
}
