package bg.softuni.serviceScheduler.vehicle.model;

import bg.softuni.serviceScheduler.carModel.model.CarModel;
import bg.softuni.serviceScheduler.services.insurance.model.Insurance;
import bg.softuni.serviceScheduler.services.vignette.model.Vignette;
import bg.softuni.serviceScheduler.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false)
    private CarModel model;
    @OneToOne(cascade = CascadeType.ALL)
    private Engine engine;
    @Column(nullable = false, unique = true)
    private String vin;
    @Column(nullable = false)
    private Year yearOfProduction;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleCategory category;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    @Column(nullable = false, unique = true)
    private String registration;
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Insurance> insurances;
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL)
    private List<Vignette> vignettes;

}
