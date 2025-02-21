package bg.softuni.servicescheduler.country.model;

import bg.softuni.servicescheduler.engine.model.Engine;
import bg.softuni.servicescheduler.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    @Basic
    private Double averageFuelConsumption;
    @Basic
    private BigDecimal initialCost;
    @Column(nullable = false)
    private BigDecimal currentCost;
    @Column(nullable = false)
    private Year yearOfProduction;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

}
