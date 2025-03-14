package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.web.validation.MileageEqualOrGreater;

@MileageEqualOrGreater(message = "New mileage must be greater or equal to old")
public record EngineMileageAddBindingModel(
        Integer oldMileage,
        Integer newMileage
) {
}
