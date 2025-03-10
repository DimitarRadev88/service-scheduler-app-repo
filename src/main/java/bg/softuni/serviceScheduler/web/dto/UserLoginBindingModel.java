package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserLoginBindingModel(
        @NotBlank(message = "Email field cannot be blank!")
        @Size(min = 2, max = 50, message = "Please enter valid username")
        String username,
        @NotBlank(message = "Password field cannot be blank!")
        @Size(min = 6, max = 20, message = "Password length must be between 6 and 20 characters")
        String password
) {

    public UserLoginBindingModel() {
        this(null, null);
    }

}
