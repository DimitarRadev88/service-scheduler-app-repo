package bg.softuni.servicescheduler.liquid.manufacturer;

import bg.softuni.servicescheduler.country.Country;
import bg.softuni.servicescheduler.liquid.coolant.Coolant;
import bg.softuni.servicescheduler.liquid.oil.EngineOil;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
public class Manufacturer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @ManyToOne
    private Country origin;
    @OneToMany(mappedBy = "manufacturer")
    private List<EngineOil> engineOils;
    @OneToMany
    private List<Coolant> coolants;

}
