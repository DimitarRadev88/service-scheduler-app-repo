package bg.softuni.serviceScheduler.insurance.model;

import bg.softuni.serviceScheduler.vehicle.model.Car;
import bg.softuni.serviceScheduler.web.dto.InsuranceAddBindingModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "insurances")
@Getter
@Setter
@NoArgsConstructor
    @AllArgsConstructor
public class Insurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Basic
    private String companyName;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private InsuranceValidity validity;
    @Column(nullable = false)
    private BigDecimal cost;
    @Column(nullable = false)
    private Boolean isValid;
    @ManyToOne
    private Car car;
    @Column(nullable = false)
    private LocalDate addedAt;

}
