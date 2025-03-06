package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.web.dto.OilChangeAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CarService {

    @Transactional
    List<CarDashboardServicesDoneViewServiceModel> getAllServicesByUser(UUID userId);

    UUID doAdd(CarAddBindingModel vehicleAdd, UUID userId);

    Map<String, List<String>> getAllBrandsWithModels();

    CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id);

    CarInfoServiceViewModel getCarInfoServiceViewModel(UUID id);

    LastServicesServiceViewModel getLastServices(Car car);

    EngineOilChangeServiceViewModel getEngineOilChangeAddViewModel(UUID engineId);

    UUID doAdd(OilChangeAddBindingModel oilChangeAdd, UUID engineId);

    void doAddMileage(EngineMileageAddBindingModel engineMileageAdd, UUID id);

    Long getOilChangesCount();
}
