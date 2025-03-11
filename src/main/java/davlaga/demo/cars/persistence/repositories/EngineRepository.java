package davlaga.demo.cars.persistence.repositories;

import davlaga.demo.cars.model.EngineDTO;
import davlaga.demo.cars.persistence.entities.Engine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EngineRepository extends JpaRepository<Engine, Long> {
    @Query("SELECT NEW davlaga.demo.cars.model.EngineDTO(e.id, e.horsePower, e.capacity) FROM Engine e WHERE e.capacity = :capacity")
    Page<EngineDTO> findEngines(double capacity, Pageable pageable);
}
