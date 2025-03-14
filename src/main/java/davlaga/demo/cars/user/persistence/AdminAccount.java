package davlaga.demo.cars.user.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "admin_account")
public class AdminAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "balance_in_cents", nullable = false)
    private long balanceInCents = 0;
}
