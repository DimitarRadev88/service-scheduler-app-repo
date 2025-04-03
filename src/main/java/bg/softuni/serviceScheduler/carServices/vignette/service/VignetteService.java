package bg.softuni.serviceScheduler.carServices.vignette.service;

import bg.softuni.serviceScheduler.web.dto.VignetteAddBindingModel;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.UUID;

public interface VignetteService {
    String doAdd(VignetteAddBindingModel vignetteAdd, UUID carId);

    boolean hasActiveVignette(UUID id);

    BigDecimal getSumVignetteCostByUserId(UUID userId);

    @Scheduled(cron = "0 0 0 * * *")
    void validateAllStartingVignettes();

    void invalidateAllExpiredVignettes();

    BigDecimal getSumVignetteCostByCarId(UUID id);
}
