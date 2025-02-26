package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UserLoginBindingModel(
        @Email(message = "Please enter valid email")
        @NotBlank(message = "Email field cannot be blank!")
        String email,
        @NotBlank(message = "Password field cannot be blank!")
        @Size(min = 6, max = 20, message = "Password length must be between 6 and 20 characters")
        String password
) {

        public UserLoginBindingModel() {
                this(null, null);
        }

}
