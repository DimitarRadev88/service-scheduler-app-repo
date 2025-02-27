package bg.softuni.serviceScheduler.vehicle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_brands")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "brand")
    List<CarModel> models;

}
