package bg.softuni.serviceScheduler.vehicle.service.dto;

import bg.softuni.serviceScheduler.vehicle.model.FuelType;

import java.util.UUID;

public record CarInfoServiceViewModel(
        UUID id,
        String makeAndModel,
        String vin,
        String registration,
        LastServicesServiceViewModel lastServices,
        CarInfoEngineViewModel engine
) {
}
