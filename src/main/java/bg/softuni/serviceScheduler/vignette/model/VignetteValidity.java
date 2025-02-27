package bg.softuni.serviceScheduler.vignette.model;

public enum VignetteValidity {

    WEEKLY(7),
    MONTHLY(30),
    THREE_MONTHLY(90),
    YEARLY(365);

    private final int days;

    private VignetteValidity(int days) {
        this.days = days;
    }

}
