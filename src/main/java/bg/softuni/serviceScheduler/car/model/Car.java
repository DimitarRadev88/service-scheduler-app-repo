package bg.softuni.serviceScheduler.car.model;

import bg.softuni.serviceScheduler.engine.model.Engine;
import bg.softuni.serviceScheduler.insurance.model.Insurance;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.vignette.model.Vignette;
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
    @ManyToOne
    @JoinColumn(nullable = false)
    private CarModel model;
    @OneToOne
    private Engine engine;
    @Column(nullable = false)
    private Year yearOfProduction;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    @OneToMany(mappedBy = "car")
    private List<Insurance> insurances;
    @OneToMany(mappedBy = "car")
    private List<Vignette> vignettes;

}
