package bg.softuni.serviceScheduler.carModel.model;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_models")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private String brandName;
    @Column(nullable = false)
    private String modelName;
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL)
    private List<Car> cars;

}
