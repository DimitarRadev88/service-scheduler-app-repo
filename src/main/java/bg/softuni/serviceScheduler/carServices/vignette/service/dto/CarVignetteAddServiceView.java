package bg.softuni.serviceScheduler.carServices.vignette.service.dto;


import java.util.UUID;

public record CarVignetteAddServiceView (
        UUID id,
        String makeAndModel,
        String registration
) {
}
