package bg.softuni.serviceScheduler.vehicle.service.dto;

import java.time.LocalDate;
import java.util.UUID;

public record InsurancePaymentDateAndIdServiceViewModel(
        UUID id,
        LocalDate date,
        Boolean isInsuranceInactive,
        Boolean isInsuranceCloseToExpiring,
        Boolean isInsuranceExpired
) {
}
