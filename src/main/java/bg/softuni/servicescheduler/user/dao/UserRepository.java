package bg.softuni.servicescheduler.user.dao;

import bg.softuni.servicescheduler.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {



}
