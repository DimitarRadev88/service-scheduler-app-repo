package bg.softuni.serviceScheduler.carServices.insurance.model;

import bg.softuni.serviceScheduler.carServices.model.CarService;
import bg.softuni.serviceScheduler.vehicle.model.Car;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insurances")
@Getter
@Setter
public class Insurance extends CarService {

    @Basic
    private String companyName;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private InsuranceValidity validity;
    @Column(nullable = false)
    private Boolean isValid;
    @ManyToOne
    private Car car;

    protected Insurance(UUID id, LocalDate addedAt, BigDecimal cost) {
        super(id, addedAt, cost);
    }

    public Insurance() {
        super(null, null, null);
    }

    public Insurance(UUID id,
                     LocalDate addedAt,
                     BigDecimal cost,
                     String companyName,
                     LocalDate startDate,
                     LocalDate endDate,
                     InsuranceValidity validity,
                     Boolean isValid,
                     Car car) {
        super(id, addedAt, cost);
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.validity = validity;
        this.isValid = isValid;
        this.car = car;
    }
}
