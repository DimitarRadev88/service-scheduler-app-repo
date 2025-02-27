package bg.softuni.serviceScheduler.engine.model;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.oilChange.model.OilChange;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "engines")
@Getter
@Setter
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private Integer displacement;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FuelType fuelType;
    @Column(nullable = false)
    private Double oilCapacity;
    @Column(nullable = false)
    private Integer mileage;
    @OneToOne
    private Car car;
    @OneToMany(mappedBy = "engine")
    private List<OilChange> oilChanges;

}
