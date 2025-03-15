package bg.softuni.serviceScheduler.carModels.service;

import bg.softuni.serviceScheduler.web.dto.CarBrandAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarModelAddBindingModel;
import jakarta.validation.Valid;

import java.util.List;

public interface CarModelService {
    List<String> getAllBrands();

    List<String> getAllModelsByBrand(String brand);

    void doAdd(CarModelAddBindingModel carModelAdd);

    void doAdd(CarBrandAddBindingModel carBrandAdd);
}
