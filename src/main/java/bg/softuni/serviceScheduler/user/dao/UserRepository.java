package bg.softuni.serviceScheduler.user.dao;

import bg.softuni.serviceScheduler.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findAllByOrderByRegistrationDateAsc();
}
