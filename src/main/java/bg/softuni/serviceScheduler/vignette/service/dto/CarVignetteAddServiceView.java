package bg.softuni.serviceScheduler.vignette.service.dto;

import bg.softuni.serviceScheduler.vignette.model.VignetteValidity;

import java.util.UUID;

public record CarVignetteAddServiceView (
        UUID id,
        String makeAndModel,
        String registration
) {
}
