package bg.softuni.servicescheduler.country.model;

import bg.softuni.servicescheduler.car.model.CarBrand;
import bg.softuni.servicescheduler.liquid.model.manufacturer.Manufacturer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "countries")
@Getter
@Setter
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @OneToMany(mappedBy = "origin")
    private List<Manufacturer> manufacturers;
    @OneToMany(mappedBy = "origin")
    private List<CarBrand> carBrands;

}
