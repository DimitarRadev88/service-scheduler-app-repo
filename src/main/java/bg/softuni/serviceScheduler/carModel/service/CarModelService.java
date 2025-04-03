package bg.softuni.serviceScheduler.carModel.service;

import bg.softuni.serviceScheduler.carModel.service.dto.CarBrandNameDto;
import bg.softuni.serviceScheduler.carModel.service.dto.CarModelNameDto;
import bg.softuni.serviceScheduler.web.dto.CarBrandAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarModelAddBindingModel;

import java.util.List;

public interface CarModelService {
    List<CarBrandNameDto> getAllBrands();

    List<CarModelNameDto> getAllModelsByBrand(String brand);

    String doAdd(CarModelAddBindingModel carModelAdd);

    void doAdd(CarBrandAddBindingModel carBrandAdd);
}
