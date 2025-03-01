package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardServicesDoneViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInfoServiceViewModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarInsuranceAddServiceView;
import bg.softuni.serviceScheduler.web.dto.VehicleAddBindingModel;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CarService {

    @Transactional
    List<CarDashboardServicesDoneViewServiceModel> getAllServices();

    void doAdd(VehicleAddBindingModel vehicleAdd, UUID userId);

    Map<String, List<String>> getAllBrandsWithModels();

    CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id);

    CarInfoServiceViewModel getCarInfoServiceViewModel(UUID id);
}
