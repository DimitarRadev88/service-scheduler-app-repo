package bg.softuni.serviceScheduler.user.service.dto;

import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardViewServiceModel;
import bg.softuni.serviceScheduler.vehicle.service.dto.CarDashboardServicesDoneViewServiceModel;

import java.time.LocalDate;
import java.util.List;

public record UserDashboardServiceModelView(
        Integer servicesDone,
        LocalDate registrationDate,
        List<CarDashboardViewServiceModel> cars,
        List<CarDashboardServicesDoneViewServiceModel> services
) {
}
