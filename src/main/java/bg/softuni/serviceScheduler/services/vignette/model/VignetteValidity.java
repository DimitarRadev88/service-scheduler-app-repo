package bg.softuni.serviceScheduler.services.vignette.model;

public enum VignetteValidity {

    WEEKEND(2),
    WEEKLY(7),
    MONTHLY(30),
    THREE_MONTHLY(90),
    YEARLY(365);

    private final int days;

    VignetteValidity(int days) {
        this.days = days;
    }

    public int getDays() {
        return days;
    }
}
