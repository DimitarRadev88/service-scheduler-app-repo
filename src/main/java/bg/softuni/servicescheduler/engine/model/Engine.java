package bg.softuni.servicescheduler.engine;

import bg.softuni.servicescheduler.car.Car;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "engines")
public class Engine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    private String code;
    @Positive
    @Column(nullable = false)
    private Double oilCapacity;
    @Positive
    @Column(nullable = false)
    private Integer oilServiceInterval;
    @Positive
    @Column(nullable = false)
    private Double coolantCapacity;
    @Positive
    @Column(nullable = false)
    private Integer coolantServiceInterval;
    @OneToMany(mappedBy = "engine")
    private List<Car> car;

}
