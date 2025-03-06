package bg.softuni.serviceScheduler.vignette.service;

import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import jakarta.validation.Valid;

import java.util.UUID;

public interface VignetteService {
    void doAdd(VignetteAddBindingModel vignetteAdd, UUID carId);
}
