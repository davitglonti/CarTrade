package davlaga.demo.cars.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EngineDTO {
    private long id;
    private long horsePower;
    private double capacity;
}
