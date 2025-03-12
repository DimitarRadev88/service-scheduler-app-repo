package bg.softuni.serviceScheduler.user.exception;

import javax.naming.AuthenticationException;

public class UsernameAlreadyExistsException extends AuthenticationException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
