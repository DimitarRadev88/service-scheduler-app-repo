package bg.softuni.serviceScheduler.vignette.model;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vignettes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vignette {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VignetteValidity validity;
    @Column(nullable = false)
    private BigDecimal cost;
    @Column(nullable = false)
    private Boolean isValid;
    @ManyToOne
    private Car car;
    @Column(nullable = false)
    private LocalDate addedAt;

}
