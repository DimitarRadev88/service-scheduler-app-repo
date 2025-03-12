package bg.softuni.serviceScheduler.user.service.dto;

import java.util.UUID;

public record UserEditProfileServiceModel(
        String username,
        String email,
        String profilePictureUrl
) {
}
