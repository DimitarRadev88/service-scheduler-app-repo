package bg.softuni.serviceScheduler.vehicle.service.dto;

import bg.softuni.serviceScheduler.engine.model.FuelType;

import java.util.UUID;

public record CarInsuranceAddServiceView(
        UUID id,
        String makeAndModel,
        FuelType fuelType,
        Integer displacement,
        String vin,
        String registration
) {
}
