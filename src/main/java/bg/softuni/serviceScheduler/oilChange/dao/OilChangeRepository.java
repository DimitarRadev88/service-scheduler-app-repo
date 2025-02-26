package bg.softuni.serviceScheduler.oilChange.dao;

import bg.softuni.serviceScheduler.oilChange.model.OilChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OilChangeRepository extends JpaRepository<OilChange, UUID> {

}
