package bg.softuni.serviceScheduler.user.exception;

import javax.naming.AuthenticationException;

public class EmailAlreadyExistsException extends AuthenticationException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
