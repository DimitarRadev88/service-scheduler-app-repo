package bg.softuni.serviceScheduler.carModel.service;

import bg.softuni.serviceScheduler.carModel.service.dto.CarBrandNameDto;
import bg.softuni.serviceScheduler.carModel.service.dto.CarModelNameDto;
import bg.softuni.serviceScheduler.config.BrandsApiConfig;
import bg.softuni.serviceScheduler.web.dto.CarBrandAddBindingModel;
import bg.softuni.serviceScheduler.web.dto.CarModelAddBindingModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest({CarModelService.class, BrandsApiConfig.class})
public class CarModelServiceImplTest {

    @Autowired
    private MockRestServiceServer server;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CarModelService carModelService;

    @Test
    public void testGetAllBrandsReturnsAllBrands() throws Exception {
        List<CarBrandNameDto> expected = new ArrayList<>(
                List.of(
                        new CarBrandNameDto("Audi"),
                        new CarBrandNameDto("BMW"),
                        new CarBrandNameDto("VW"),
                        new CarBrandNameDto("Suzuki")
                )
        );

        server.expect(requestTo("http://localhost:8081/brands/all")).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expected), MediaType.APPLICATION_JSON));

        List<CarBrandNameDto> actual = carModelService.getAllBrands();

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllBrandsReturnsEmptyListWhenServerErrorIsThrown() {
        server.expect(requestTo("http://localhost:8081/brands/all")).andExpect(method(HttpMethod.GET))
                .andRespond(withNoContent());

        List<CarBrandNameDto> actual = carModelService.getAllBrands();

        assertEquals(0, actual.size());
    }

    @Test
    public void testGetAllModelsByBrandReturnsAllModels() throws JsonProcessingException {
        String brand = "Audi";

        List<CarModelNameDto> expected = new ArrayList<>(
                List.of(
                        new CarModelNameDto("A1"),
                        new CarModelNameDto("A2"),
                        new CarModelNameDto("A3"),
                        new CarModelNameDto("A4"),
                        new CarModelNameDto("A5"),
                        new CarModelNameDto("A6"),
                        new CarModelNameDto("A7"),
                        new CarModelNameDto("A8")
                )
        );

        server.expect(requestTo("http://localhost:8081/models/" + brand)).andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(expected), MediaType.APPLICATION_JSON));

        List<CarModelNameDto> actual = carModelService.getAllModelsByBrand(brand);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetAllCarsReturnsEmptyListWhenServerErrorIsThrown() {
        String brand = "missing";

        server.expect(requestTo("http://localhost:8081/models/" + brand)).andExpect(method(HttpMethod.GET))
                .andRespond(withNoContent());

        List<CarModelNameDto> actual = carModelService.getAllModelsByBrand(brand);

        assertEquals(0, actual.size());
    }

    @Test
    public void testDoAddWithCarModelHandlesConflict() {
        server.expect(requestTo("http://localhost:8081/models/add")).andExpect(method(HttpMethod.POST))
                .andRespond(withRequestConflict());

        CarModelAddBindingModel existingModel = new CarModelAddBindingModel("Audi", "A4");

        carModelService.doAdd(existingModel);
    }

    @Test
    public void testDoAddWithCarModelPosts() {
        server.expect(requestTo("http://localhost:8081/models/add")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        carModelService.doAdd(new CarModelAddBindingModel("Audi", "B101"));
    }

    @Test
    public void testDoAddWithCarBrandHandlesConflict() {
        server.expect(requestTo("http://localhost:8081/brands/add")).andExpect(method(HttpMethod.POST))
                .andRespond(withRequestConflict());

        CarBrandAddBindingModel existingBrand = new CarBrandAddBindingModel("Audi");

        carModelService.doAdd(existingBrand);
    }

}