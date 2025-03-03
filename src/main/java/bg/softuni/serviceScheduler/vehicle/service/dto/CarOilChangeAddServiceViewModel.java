package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.util.UUID;

public record CarOilChangeAddServiceViewModel(
        UUID id,
        String makeAndModel,
        String vin
) {
}
