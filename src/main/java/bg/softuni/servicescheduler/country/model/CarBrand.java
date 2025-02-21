package bg.softuni.servicescheduler.country.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "car_brands")
@Getter
@Setter
public class CarBrand {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @ManyToOne
    private Country origin;
    @OneToMany(mappedBy = "brand")
    private List<Car> cars;

}
