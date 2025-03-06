package bg.softuni.serviceScheduler.vehicle.service.dto;

import bg.softuni.serviceScheduler.vehicle.model.FuelType;

import java.util.UUID;

public record CarInfoEngineViewModel(
        UUID id,
        FuelType fuelType,
        Integer displacement,
        Integer mileage,
        Integer engineOilLifePercent,
        Integer changeInterval,
        Integer oilChangeMileage
) {
}
