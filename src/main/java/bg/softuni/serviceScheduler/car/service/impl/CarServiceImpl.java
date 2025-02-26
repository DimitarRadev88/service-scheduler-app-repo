package bg.softuni.serviceScheduler.car.service.impl;

import bg.softuni.serviceScheduler.car.dao.CarRepository;
import bg.softuni.serviceScheduler.car.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    @Autowired
    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

}
