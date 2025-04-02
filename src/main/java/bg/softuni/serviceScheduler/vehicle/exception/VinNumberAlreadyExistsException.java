package bg.softuni.serviceScheduler.vehicle.exception;

public class VinNumberAlreadyExistsException extends RuntimeException {
    public VinNumberAlreadyExistsException(String message) {
        super(message);
    }
}
