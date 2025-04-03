package bg.softuni.serviceScheduler.web.dto;

import bg.softuni.serviceScheduler.vehicle.model.FuelType;
import bg.softuni.serviceScheduler.vehicle.model.VehicleCategory;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.time.Year;

public record CarAddBindingModel(
        @NotBlank(message = "You must select vehicle brand")
        String brand,
        @NotBlank(message = "You must select vehicle model")
        String model,
        @Size(max = 15, message = "Max 15 characters for Trim")
        String trim,
        @NotNull(message = "Please select model year")
        @PastOrPresent(message = "Please enter valid year")
        Year year,
        @NotNull(message = "Vin cannot be empty")
        @Length(min = 17, max = 17, message = "Vin length must be exactly 17 characters long")
        String vin,
        @Size(min = 7, max = 8, message = "Vehicle registration number is between 7 and 8 characters long")
        String registration,
        @NotNull(message = "Please select vehicle category")
        VehicleCategory category,
        @NotNull(message = "Please select engine fuel type")
        FuelType fuelType,
        @NotNull(message = "Please enter engine displacement")
        @Min(value = 600, message = "Displacement must be over 600cc")
        @Max(value = 8000, message = "Displacement must be under 8000")
        Integer displacement,
        @NotNull(message = "Please enter engine oil capacity")
        @Positive(message = "Engine oil capacity must be greater than 0")
        Double oilCapacity,
        @NotNull(message = "Please enter vehicle mileage")
        @PositiveOrZero(message = "Vehicle mileage must be equal or greater than 0")
        Integer mileage,
        @Size(max = 20, message = "Oil filter number must be under 20 characters")
        String oilFilterNumber
) {

    public CarAddBindingModel(String brand) {
        this(brand, null, null, null, null, null, null, null, null, null, null, null);
    }
}
