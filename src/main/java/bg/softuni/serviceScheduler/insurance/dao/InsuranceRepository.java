package bg.softuni.serviceScheduler.insurance.dao;

import bg.softuni.serviceScheduler.insurance.model.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface InsuranceRepository extends JpaRepository<Insurance, UUID> {

}
