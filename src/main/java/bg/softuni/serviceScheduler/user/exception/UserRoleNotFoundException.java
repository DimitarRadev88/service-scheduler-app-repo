package bg.softuni.serviceScheduler.user.exception;

public class UserRoleNotFoundException extends RuntimeException {
    public UserRoleNotFoundException(String message) {
        super(message);
    }
}
