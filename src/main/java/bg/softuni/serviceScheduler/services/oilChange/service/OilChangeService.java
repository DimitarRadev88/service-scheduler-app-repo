package bg.softuni.serviceScheduler.services.oilChange.service;

import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import jakarta.transaction.Transactional;

import java.util.UUID;

public interface OilChangeService {
    @Transactional
    UUID doAdd(OilChangeAddBindingModel oilChangeAdd, UUID engineId);

    Long getOilChangesCount();
}
