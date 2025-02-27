package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.web.dto.VehicleAddBindingModel;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface CarService {

    List<CarDashboardViewServiceModel> getCarDashboardServiceModels();

    @Transactional
    List<CarServicesDoneViewServiceModel> getAllServices();

    void doAdd(@Valid VehicleAddBindingModel vehicleAdd, UUID userId);
}
