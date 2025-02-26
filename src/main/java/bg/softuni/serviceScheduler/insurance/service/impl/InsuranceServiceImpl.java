package bg.softuni.serviceScheduler.insurance.service.impl;

import bg.softuni.serviceScheduler.insurance.dao.InsuranceRepository;
import bg.softuni.serviceScheduler.insurance.service.InsuranceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InsuranceServiceImpl implements InsuranceService {

    private final InsuranceRepository insuranceRepository;

    @Autowired
    public InsuranceServiceImpl(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

}
