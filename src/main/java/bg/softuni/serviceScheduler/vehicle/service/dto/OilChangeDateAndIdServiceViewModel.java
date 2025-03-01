package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record OilChangeDateAndIdServiceViewModel(
        UUID id,
        LocalDate date,
        Boolean IsCloseToChange,
        Boolean isInNeedOfChanging
) {
}
