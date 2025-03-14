package bg.softuni.serviceScheduler.carModels.service;

import java.util.List;

public interface CarModelService {
    List<String> getAllBrands();

    List<String> getAllModelsByBrand(String brand);
}
