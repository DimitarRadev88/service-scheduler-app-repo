package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserProfileEditBindingModel(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 2, max = 50, message = "Username length must be between 2 and 50 characters")
        String username,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Please enter valid email")
        String email,
        String profilePictureUrl
) {

}
