package davlaga.demo.cars.persistence.repositories;

import davlaga.demo.cars.persistence.entities.TaxAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxAccountRepository extends JpaRepository<TaxAccount, Long> {
}
