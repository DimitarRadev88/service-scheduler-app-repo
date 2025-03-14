package bg.softuni.serviceScheduler.util;

import bg.softuni.serviceScheduler.user.dao.UserRoleRepository;
import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class ConsoleRunner implements CommandLineRunner {

    private final UserRoleRepository userRoleRepository;

    @Autowired
    public ConsoleRunner(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRoleRepository.count() == 0) {
            initUserRoles();
        }
    }

    private void initUserRoles() {
        Arrays.stream(UserRoleEnumeration.values()).forEach(role -> {
            UserRole userRole = new UserRole();
            userRole.setRole(role);
            userRoleRepository.save(userRole);
        });

    }

}
