package bg.softuni.serviceScheduler.user.service.dto;

import java.time.LocalDate;

public record UserProfileViewServiceModel(
    String username,
    String email,
    LocalDate registrationDate,
    String profilePictureUrl
) {
}
