package bg.softuni.serviceScheduler.services.vignette.service.dto;


import java.util.UUID;

public record CarVignetteAddServiceView (
        UUID id,
        String makeAndModel,
        String registration
) {
}
