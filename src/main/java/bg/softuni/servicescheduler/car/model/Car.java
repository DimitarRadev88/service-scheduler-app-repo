package bg.softuni.servicescheduler.car;

import bg.softuni.servicescheduler.engine.Engine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;

@Entity
@Table(name = "cars")
@Getter
@Setter
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private CarBrand brand;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Engine engine;
    private Double averageFuelConsumption;
    private BigDecimal initialCost;
    private BigDecimal currentCost;
    private Year yearOfProduction;

}
