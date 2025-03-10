package bg.softuni.serviceScheduler.util;

import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import bg.softuni.serviceScheduler.util.dto.BrandWithModel;
import bg.softuni.serviceScheduler.vehicle.dao.CarBrandRepository;
import bg.softuni.serviceScheduler.vehicle.dao.CarModelRepository;
import bg.softuni.serviceScheduler.vehicle.model.CarBrand;
import bg.softuni.serviceScheduler.vehicle.model.CarModel;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class ConsoleRunner implements CommandLineRunner {

    private final CarBrandRepository carBrandRepository;
    private final Gson gson;
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public ConsoleRunner(CarBrandRepository carBrandRepository, Gson gson, UserRoleRepository userRoleRepository) {
        this.carBrandRepository = carBrandRepository;
        this.gson = gson;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRoleRepository.count() == 0) {
            initUserRoles();
        }
        if (carBrandRepository.count() == 0) {
            BrandWithModel[] brandWithModels = readCarBrands();
            initCarBrands(brandWithModels);
        }

    }

    private void initUserRoles() {
        Arrays.stream(UserRoleEnumeration.values()).forEach(role -> {
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        });

    }

    private BrandWithModel[] readCarBrands() throws IOException {

        String cars = Files.readString(Path.of("src/main/resources/json/brands.json"));

        return gson.fromJson(cars, BrandWithModel[].class);
    }

    public void initCarBrands(BrandWithModel[] brandWithModels) {
        List<CarBrand> list = Arrays.stream(brandWithModels).map(brandWithModel -> {
            CarBrand carBrand = new CarBrand();
            carBrand.setName(brandWithModel.getBrand());
            carBrand.setModels(
                    brandWithModel.getModels().stream().map(model -> {
                        CarModel carModel = new CarModel();
                        carModel.setBrand(carBrand);
                        carModel.setName(model);
                        return carModel;
                    }).sorted(Comparator.comparing(CarModel::getName)).toList()
            );
            return carBrand;
        }).sorted(Comparator.comparing(CarBrand::getName)).toList();

        carBrandRepository.saveAll(list);
    }
}
