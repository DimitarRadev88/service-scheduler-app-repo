package bg.softuni.serviceScheduler.user.service.dto;

import java.util.UUID;

public record CarInsuranceAddSelectView(
        UUID id,
        String makeAndModel
) {
}
