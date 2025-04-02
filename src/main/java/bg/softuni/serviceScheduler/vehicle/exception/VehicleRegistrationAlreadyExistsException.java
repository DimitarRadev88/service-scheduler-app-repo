package bg.softuni.serviceScheduler.vehicle.exception;

public class VehicleRegistrationAlreadyExistsException extends RuntimeException {
    public VehicleRegistrationAlreadyExistsException(String message) {
        super(message);
    }
}
