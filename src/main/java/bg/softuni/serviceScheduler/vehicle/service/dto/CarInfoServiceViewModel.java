package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.util.UUID;

public record CarInfoServiceViewModel(
        UUID id,
        String makeAndModel,
        String vin,
        String registration,
        LastServicesServiceViewModel lastServices,
        CarInfoEngineViewModel engine
) {
    public CarInfoServiceViewModel() {
        this(null, null, null, null, null, null);
    }

}
