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
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class CarServiceIntegrationTests {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    public void testTest() {

        entityManager.persist(new User(null, "Username", "password", "email", LocalDateTime.now(), "url", new ArrayList<>(), List.of(userRoleRepository.findByRole(UserRoleEnumeration.USER))));

        assertTrue(userRepository.existsByUsername("Username"));
    }
}
