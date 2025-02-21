package bg.softuni.servicescheduler.liquid.oil;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "engine_oils")
@Getter
@Setter
public class EngineOil extends Oil {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EngineOilViscosity viscosity;
    @Column(nullable = false)
    private BigDecimal pricePerLiter;

}
