package bg.softuni.serviceScheduler.vehicle.service;

import bg.softuni.serviceScheduler.user.dao.UserRepository;
import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.model.User;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase
public class CarServiceImplIT {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    public void testTest() {

        userRoleRepository.save(new UserRole(null, UserRoleEnumeration.USER));
        userRoleRepository.save(new UserRole(null, UserRoleEnumeration.ADMIN));

        userRepository.save(new User(null, "Username", "password", "email", LocalDateTime.now(), "url", new ArrayList<>(), new ArrayList<>(List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER)))));

        assertTrue(userRepository.existsByUsername("Username"));
    }

}
