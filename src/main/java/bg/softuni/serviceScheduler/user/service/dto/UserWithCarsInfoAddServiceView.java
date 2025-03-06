package bg.softuni.serviceScheduler.user.service.dto;

import java.util.List;
import java.util.UUID;

public record UserWithCarsInfoAddServiceView(
        UUID id,
        List<CarInsuranceAddSelectView> cars
) {
}
