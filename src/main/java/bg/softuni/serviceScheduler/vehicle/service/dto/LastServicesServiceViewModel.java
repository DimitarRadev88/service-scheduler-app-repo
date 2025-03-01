package bg.softuni.serviceScheduler.vehicle.service.dto;

public record LastServicesServiceViewModel(
        OilChangeDateAndIdServiceViewModel oilChange,
        InsurancePaymentDateAndIdServiceViewModel insurance,
        VignetteDateAndIdServiceViewModel vignette
) {
}
