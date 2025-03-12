package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.web.validation.PasswordsMatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@PasswordsMatch
public record UserRegisterBindingModel(
        @NotBlank(message = "Username length must be between 2 and 50 characters")
        @Size(min = 2, max = 50, message = "Username length must be between 2 and 50 characters")
        String username,
        @NotBlank(message = "Please enter valid email")
        @Email(message = "Please enter valid email")
        String email,
        @NotBlank(message = "Password length must be between 6 and 20 characters")
        @Size(min = 6, max = 20, message = "Password length must be between 6 and 20 characters")
        String password,
        @NotBlank(message = "Password length must be between 6 and 20 characters")
        @Size(min = 6, max = 20, message = "Password length must be between 6 and 20 characters")
        String confirmPassword
) {

    public UserRegisterBindingModel() {
        this(null, null, null, null);
    }

}
