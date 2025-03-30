package bg.softuni.serviceScheduler.user.service.dto;

import java.util.UUID;

public record CarServiceAddSelectView(
        UUID id,
        String makeAndModel
) {
}
