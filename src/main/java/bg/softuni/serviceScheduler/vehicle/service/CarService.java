package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.carServices.vignette.service.dto.CarVignetteAddServiceView;
import bg.softuni.serviceScheduler.user.service.dto.CarServiceAddSelectView;
import bg.softuni.serviceScheduler.vehicle.service.dto.*;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.EngineMileageAddBindingModel;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CarService {

    @Transactional
    List<CarDashboardServicesDoneViewServiceModel> getAllServicesByUser(UUID userId);

    UUID doAdd(CarAddBindingModel vehicleAdd, UUID userId);

    CarInsuranceAddServiceView getCarInsuranceAddServiceView(UUID id);

    CarInfoServiceViewModel getCarInfoServiceViewModel(UUID id);

    EngineOilChangeServiceViewModel getEngineOilChangeAddViewModel(UUID engineId);

    void doAddMileage(EngineMileageAddBindingModel engineMileageAdd, UUID id);

    void doDelete(UUID id);

    CarVignetteAddServiceView getCarVignetteAddServiceView(UUID id);

    BigDecimal getAllServicesCostByUser(UUID userId);

    List<CarDashboardViewServiceModel> getAllCarDashboardServiceViewModelsByUser(UUID id);

    List<CarServiceAddSelectView> getCarInsuranceAddSelectView(UUID id);
}
