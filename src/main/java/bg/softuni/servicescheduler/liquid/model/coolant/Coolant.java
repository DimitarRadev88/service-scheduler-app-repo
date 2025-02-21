package bg.softuni.servicescheduler.liquid.model.coolant;

import bg.softuni.servicescheduler.liquid.model.manufacturer.Manufacturer;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "coolants")
public class Coolant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private Manufacturer manufacturer;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoolantColor color;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoolantSpecification specification;

}
