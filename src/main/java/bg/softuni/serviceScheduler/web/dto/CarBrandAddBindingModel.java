package bg.softuni.serviceScheduler.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CarBrandAddBindingModel(
        @NotBlank(message = "Brand name cannot be blank")
        String name) {

}
