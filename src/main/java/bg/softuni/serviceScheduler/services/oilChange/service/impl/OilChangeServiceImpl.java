package bg.softuni.serviceScheduler.services.oilChange.service.impl;

import bg.softuni.serviceScheduler.services.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.services.oilChange.model.OilChange;
import bg.softuni.serviceScheduler.services.oilChange.service.OilChangeService;
import bg.softuni.serviceScheduler.vehicle.dao.EngineRepository;
import bg.softuni.serviceScheduler.vehicle.exception.EngineNotFoundException;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class OilChangeServiceImpl implements OilChangeService {

    private final OilChangeRepository oilChangeRepository;
    private final EngineRepository engineRepository;

    @Autowired
    public OilChangeServiceImpl(OilChangeRepository oilChangeRepository, EngineRepository engineRepository) {
        this.oilChangeRepository = oilChangeRepository;
        this.engineRepository = engineRepository;
    }

    @Transactional
    @Override
    public UUID doAdd(OilChangeAddBindingModel oilChangeAdd, UUID engineId) {
        Engine engine = engineRepository.findById(engineId).orElseThrow(() -> new EngineNotFoundException("Engine not found"));

        OilChange oilChange = new OilChange(
                null,
                engine,
                oilChangeAdd.cost(),
                LocalDate.now(),
                oilChangeAdd.changeMileage(),
                oilChangeAdd.changeInterval(),
                oilChangeAdd.changeDate()
        );

        OilChange save = oilChangeRepository.save(oilChange);
        engine.getOilChanges().add(save);

        engineRepository.save(engine);
        return engine.getCar().getId();
    }

    @Override
    public Long getOilChangesCount() {
        return oilChangeRepository.count();
    }

}
