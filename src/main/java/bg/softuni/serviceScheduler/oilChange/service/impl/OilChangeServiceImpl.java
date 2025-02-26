package bg.softuni.serviceScheduler.oilChange.service.impl;

import bg.softuni.serviceScheduler.oilChange.dao.OilChangeRepository;
import bg.softuni.serviceScheduler.oilChange.service.OilChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OilChangeServiceImpl implements OilChangeService {

    private final OilChangeRepository oilChangeRepository;

    @Autowired
    public OilChangeServiceImpl(OilChangeRepository oilChangeRepository) {
        this.oilChangeRepository = oilChangeRepository;
    }

}
