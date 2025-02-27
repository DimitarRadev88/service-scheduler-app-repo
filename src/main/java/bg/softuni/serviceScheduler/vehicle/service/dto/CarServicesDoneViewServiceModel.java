package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CarServicesDoneViewServiceModel(
        String name,
        LocalDate date,
        BigDecimal cost
) {
}
