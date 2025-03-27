package bg.softuni.serviceScheduler.carServices.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class CarService {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private LocalDate addedAt;
    @Column(nullable = false)
    private BigDecimal cost;

    protected CarService(UUID id, LocalDate addedAt, BigDecimal cost) {
        this.id = id;
        this.addedAt = addedAt;
        this.cost = cost;
    }

}
