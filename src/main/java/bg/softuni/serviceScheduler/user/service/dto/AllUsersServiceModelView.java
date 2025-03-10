package bg.softuni.serviceScheduler.user.service.dto;

import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;

import java.util.UUID;

public record AllUsersServiceModelView(
        UUID id,
        String username,
        UserRoleEnumeration role
) {
}
