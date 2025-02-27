package bg.softuni.serviceScheduler.insurance.model;

import lombok.Getter;

@Getter
public enum InsuranceValidity {

    MONTHLY(30),
    THREE_MONTHLY(90),
    SIX_MONTHLY(180),
    YEARLY(365),
    ;

    private final int days;

    InsuranceValidity(int days) {
        this.days = days;
    }

}
