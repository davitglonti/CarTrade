package davlaga.demo.cars.persistence.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "tax_account")
public class TaxAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance_in_cents", nullable = false)
    private long balanceInCents = 0;
}