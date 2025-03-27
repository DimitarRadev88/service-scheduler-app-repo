package bg.softuni.serviceScheduler.carServices.vignette.service;

import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;

import java.math.BigDecimal;
import java.util.UUID;

public interface VignetteService {
    void doAdd(VignetteAddBindingModel vignetteAdd, UUID carId);

    boolean hasActiveVignette(UUID id);

    BigDecimal getSumVignetteCostByUserId(UUID userId);

    void changeAllExpiredVignettesIsValidStatus();

}
