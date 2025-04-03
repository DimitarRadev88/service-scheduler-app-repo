package bg.softuni.serviceScheduler.carServices.oilChange.model;

import bg.softuni.serviceScheduler.carServices.model.CarService;
import bg.softuni.serviceScheduler.vehicle.model.Engine;
import bg.softuni.serviceScheduler.web.dto.CarAddBindingModel;
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
public class OilChange extends CarService {

    @ManyToOne
    @JoinColumn(nullable = false)
    private Engine engine;
    @Column(nullable = false)
    private Integer mileage;
    @Column(nullable = false)
    private Integer changeInterval;
    @Column(nullable = false)
    private LocalDate changeDate;

    public OilChange(UUID id, LocalDate addedAt, BigDecimal cost, Engine engine, Integer mileage, Integer changeInterval, LocalDate changeDate) {
        super(id, addedAt, cost);
        this.engine = engine;
        this.mileage = mileage;
        this.changeInterval = changeInterval;
        this.changeDate = changeDate;
    }

    public OilChange() {
        super(null, null, null);
    }
}
