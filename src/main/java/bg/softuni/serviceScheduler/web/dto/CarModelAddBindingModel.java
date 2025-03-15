package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CarModelAddBindingModel(
        @NotBlank(message = "Car brand cannot be blank")
        String brand,
        @NotBlank(message = "Car model cannot be blank")
        String model
) {
}
