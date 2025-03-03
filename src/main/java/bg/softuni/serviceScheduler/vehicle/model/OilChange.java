package bg.softuni.serviceScheduler.vehicle.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OilChange {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Engine engine;
    @Column(nullable = false)
    private BigDecimal cost;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private Integer mileage;
    @Column(nullable = false)
    private Integer changeInterval;
    @Column(nullable = false)
    private LocalDate changeDate;

}
