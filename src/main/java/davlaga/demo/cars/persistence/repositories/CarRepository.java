package davlaga.demo.cars.persistence.repositories;

import davlaga.demo.cars.model.CarDTO;
import davlaga.demo.cars.persistence.entities.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository  extends JpaRepository<Car, Long>{
    @Query("SELECT NEW davlaga.demo.cars.model.CarDTO(c.id, c.model, c.year, c.driveable, " +
            "new davlaga.demo.cars.model.EngineDTO(e.id, e.horsePower, e.capacity)) " +
            "FROM Car c JOIN c.engine e")
    Page<CarDTO> findCars(Pageable pageable);

    @Query("SELECT c FROM Car c JOIN c.owners u WHERE u.username = :username")
    Page<Car> findByOwnerUsername(String username, Pageable pageable);
}
