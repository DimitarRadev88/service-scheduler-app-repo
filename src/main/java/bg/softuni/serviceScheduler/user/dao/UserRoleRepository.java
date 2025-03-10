package bg.softuni.serviceScheduler.user.dao;

import bg.softuni.serviceScheduler.user.model.UserRole;
import bg.softuni.serviceScheduler.user.model.UserRoleEnumeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    UserRole findByRole(UserRoleEnumeration role);
}
