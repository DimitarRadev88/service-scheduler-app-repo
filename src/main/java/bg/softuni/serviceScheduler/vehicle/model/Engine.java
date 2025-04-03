package bg.softuni.serviceScheduler.vehicle.model;

import bg.softuni.serviceScheduler.carServices.oilChange.model.OilChange;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "engines")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    @Basic
    private String oilFilter;
    @OneToOne
    private Car car;
    @OneToMany(mappedBy = "engine", cascade = CascadeType.ALL)
    @OrderColumn
    private List<OilChange> oilChanges;

}
