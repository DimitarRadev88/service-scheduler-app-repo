package bg.softuni.serviceScheduler.user.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
public class ServiceSchedulerUserDetails extends User {

    private final UUID id;

    public ServiceSchedulerUserDetails(UUID id,
                                       String username,
                                       String password,
                                       Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

}
