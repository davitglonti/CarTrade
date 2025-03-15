package davlaga.demo.cars.persistence.entities;
import davlaga.demo.cars.user.persistence.AppUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="car")
@SequenceGenerator(name="car_seq_gen", sequenceName = "car_seq", allocationSize = 1)
@Getter
@Setter
public class Car {
    @Id
    @GeneratedValue(generator = "car_seq_gen", strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(name = "model")
    private String model;

    @Column(name = "year")
    private int year;

    @Column(name = "is_driveable")
    private boolean driveable;

    @Column(name = "price_in_cents", nullable = false)
    private long priceInCents;

    @ManyToOne
    @JoinColumn(name = "engine_id")
    private Engine engine;

    @ManyToMany(mappedBy = "ownedCars")
    private Set<AppUser> owners = new HashSet<>();

    public boolean isDrivable() {
        return driveable;
    }

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "reserved_until")
    private LocalDateTime reservedUntil;

    @ManyToOne
    @JoinColumn(name = "reserved_by")
    private AppUser reservedBy;


}

