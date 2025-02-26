package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRegisterBindingModel(
        @NotBlank
        String firstName,
        @NotBlank
        String lastName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        @Size(min = 6, max = 20)
        String password,
        @NotBlank
        @Size(min = 6, max = 20)
        String confirmPassword
) {

        public UserRegisterBindingModel() {
                this(null, null, null, null, null);
        }

}
