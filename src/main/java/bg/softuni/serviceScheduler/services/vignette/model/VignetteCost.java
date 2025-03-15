package bg.softuni.serviceScheduler.services.vignette.model;

import java.math.BigDecimal;

public enum VignetteCost {

    WEEKEND(new BigDecimal("9")),
    WEEKLY(new BigDecimal("13")),
    MONTHLY(new BigDecimal("27")),
    THREE_MONTHLY(new BigDecimal("48")),
    YEARLY(new BigDecimal("87"));

    private final BigDecimal cost;

    VignetteCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
