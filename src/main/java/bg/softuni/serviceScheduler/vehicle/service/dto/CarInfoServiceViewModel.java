package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.util.UUID;

public record CarInfoServiceViewModel(
        UUID id,
        String makeAndModel,
        Integer engineOilLifePercent,
        LastServicesServiceViewModel lastServices,
        CarInfoEngineViewModel engine
) {
}
