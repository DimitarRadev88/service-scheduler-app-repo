package bg.softuni.serviceScheduler.carModel.service.impl;

import bg.softuni.serviceScheduler.carModel.dto.SavedCarModel;
import bg.softuni.serviceScheduler.carModel.exception.CarModelAddException;
import bg.softuni.serviceScheduler.carModel.service.CarModelService;
import bg.softuni.serviceScheduler.carModel.service.dto.CarBrandNameDto;
import bg.softuni.serviceScheduler.carModel.service.dto.CarModelNameDto;
import bg.softuni.serviceScheduler.config.BrandsApiConfig;
import bg.softuni.serviceScheduler.web.dto.CarBrandAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarModelAddBindingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CarModelServiceImpl implements CarModelService {

    private final RestClient restClient;

    @Autowired
    public CarModelServiceImpl(RestClient.Builder builder, BrandsApiConfig brandsApiConfig) {
        this.restClient = builder
                .baseUrl(brandsApiConfig.getBaseUrl())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<CarBrandNameDto> getAllBrands() {
        List<CarBrandNameDto> body = restClient
                .get()
                .uri("/brands/all")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return body == null ? new ArrayList<>() : body;
    }

    @Override
    public List<CarModelNameDto> getAllModelsByBrand(String brand) {
        try {
            List<CarModelNameDto> body = restClient
                    .get()
                    .uri("/models/" + brand)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
            if (body != null) {
                return body;
            }
        } catch (HttpServerErrorException e) {
            log.error(e.getResponseBodyAsString());
        }

        return new ArrayList<>();
    }

    @Override
    public String doAdd(CarModelAddBindingModel carModelAdd) {
        log.info("Creating car model: {}", carModelAdd);

        try {
            SavedCarModel savedCarModel = restClient.post()
                    .uri("/models/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(carModelAdd)
                    .retrieve()
                    .body(SavedCarModel.class);

            return savedCarModel.brandName()+ " " + savedCarModel.modelName();
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
            throw new CarModelAddException(e.getMessage());
        }

    }

    @Override
    public void doAdd(CarBrandAddBindingModel carBrandAdd) {
        log.info("Creating car brand: {}", carBrandAdd);

        try {
            restClient
                    .post()
                    .uri("/brands/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(carBrandAdd)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            log.error(e.getResponseBodyAsString());
        }

    }
}
