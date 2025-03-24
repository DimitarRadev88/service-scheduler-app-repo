package bg.softuni.serviceScheduler.vehicle.exception;

public class EngineNotFoundException extends RuntimeException {
    public EngineNotFoundException(String message) {
        super(message);
    }
}
