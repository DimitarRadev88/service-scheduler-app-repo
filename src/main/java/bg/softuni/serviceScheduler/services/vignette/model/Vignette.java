package bg.softuni.serviceScheduler.services.vignette.model;

import bg.softuni.serviceScheduler.services.model.CarService;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vignettes")
@Getter
@Setter
public class Vignette extends CarService {

    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VignetteValidity validity;
    @Column(nullable = false)
    private Boolean isValid;
    @ManyToOne
    private Car car;

    public Vignette(UUID id, LocalDate addedAt, BigDecimal cost, LocalDate startDate, LocalDate endDate, VignetteValidity validity, Boolean isValid, Car car) {
        super(id, addedAt, cost);
        this.startDate = startDate;
        this.endDate = endDate;
        this.validity = validity;
        this.isValid = isValid;
        this.car = car;
    }

    public Vignette() {
        super(null, null, null);
    }

}
