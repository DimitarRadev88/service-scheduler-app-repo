package bg.softuni.serviceScheduler.carModels.service.impl;

import bg.softuni.serviceScheduler.carModels.dao.CarModelRepository;
import bg.softuni.serviceScheduler.carModels.service.CarModelService;
import bg.softuni.serviceScheduler.carModels.service.dto.CarBrandNameDto;
import bg.softuni.serviceScheduler.carModels.service.dto.CarModelNameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarModelServiceImpl implements CarModelService {

    private final CarModelRepository carModelRepository;
    private final RestClient restClient;

    @Autowired
    public CarModelServiceImpl(CarModelRepository carModelRepository, @Qualifier("carBrandsRestClient") RestClient restClient) {
        this.carModelRepository = carModelRepository;
        this.restClient = restClient;
    }

    @Override
    public List<String> getAllBrands() {
        CarBrandNameDto[] body = restClient
                .get()
                .uri("/brands/all")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CarBrandNameDto[].class);

        return Arrays.stream(body)
                .map(CarBrandNameDto::name)
                .toList();

    }

    @Override
    public List<String> getAllModelsByBrand(String brand) {
        CarModelNameDto[] body = restClient
                .get()
                .uri("/models/" + brand)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(CarModelNameDto[].class);

        return Arrays.stream(body)
                .map(CarModelNameDto::name)
                .collect(Collectors.toList());
    }
}
