package bg.softuni.serviceScheduler.engine.service.impl;

import bg.softuni.serviceScheduler.engine.dao.EngineRepository;
import bg.softuni.serviceScheduler.engine.service.EngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EngineServiceImpl implements EngineService {

    private final EngineRepository engineRepository;

    @Autowired
    public EngineServiceImpl(EngineRepository engineRepository) {
        this.engineRepository = engineRepository;
    }

}
