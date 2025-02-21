package bg.softuni.servicescheduler.liquid.model.oil;

import bg.softuni.servicescheduler.liquid.model.manufacturer.Manufacturer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "engine_oils")
@Getter
@Setter
public class EngineOil {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private Manufacturer manufacturer;
    @Column(nullable = false)
    private BigDecimal pricePerLiter;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineOilViscosity viscosity;

}
