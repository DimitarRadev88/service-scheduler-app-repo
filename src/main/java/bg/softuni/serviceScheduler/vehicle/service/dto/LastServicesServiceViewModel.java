package bg.softuni.serviceScheduler.vehicle.service.dto;

public record LastServicesServiceViewModel(
        CarOilChangeDateAndIdServiceViewModel oilChange,
        InsurancePaymentDateAndIdServiceViewModel insurance,
        VignetteDateAndIdServiceViewModel vignette
) {
}
