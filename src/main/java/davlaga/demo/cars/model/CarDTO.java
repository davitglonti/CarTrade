package davlaga.demo.cars.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor

public class CarDTO {
    private long id;
    private String model;
    private int year;
    private boolean drivable;
    private EngineDTO engine;
    private long priceInCents;
    private Set<String> ownerUsernames;
    private String photoUrl;

    public CarDTO(long id, String model, int year, boolean drivable, EngineDTO engine) {
        this.id = id;
        this.model = model;
        this.year = year;
        this.drivable = drivable;
        this.engine = engine;
        this.priceInCents = 0;
        this.ownerUsernames = null;
        this.photoUrl = null;
    }
    }
