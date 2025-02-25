package bg.softuni.serviceScheduler.user.dao;

import bg.softuni.serviceScheduler.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {



}
