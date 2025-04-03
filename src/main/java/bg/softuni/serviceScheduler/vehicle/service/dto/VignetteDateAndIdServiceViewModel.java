package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VignetteDateAndIdServiceViewModel(
        UUID id,
        LocalDate date,
        Boolean isVignetteInactive,
        Boolean isVignetteCloseToExpiring,
        Boolean isVignetteExpired
) {
}
