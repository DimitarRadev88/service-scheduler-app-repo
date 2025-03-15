package bg.softuni.serviceScheduler.services.insurance.model;

import lombok.Getter;

@Getter
public enum InsuranceValidity {

    MONTHLY(30, "1 Month"),
    THREE_MONTHLY(90, "3 Months"),
    SIX_MONTHLY(180, "6 Months"),
    YEARLY(365, "1 Year"),
    ;

    private final int days;
    private final String periodName;

    InsuranceValidity(int days, String periodName) {
        this.days = days;
        this.periodName = periodName;
    }

}
