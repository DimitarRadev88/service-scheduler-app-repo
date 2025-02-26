package bg.softuni.serviceScheduler.engine.dao;

import bg.softuni.serviceScheduler.engine.model.Engine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EngineRepository extends JpaRepository<Engine, UUID> {
}
