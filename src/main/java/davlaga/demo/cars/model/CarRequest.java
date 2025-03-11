package davlaga.demo.cars.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarRequest {
    @NotBlank
    @Size(max= 40)
    private String model;
    @Min(1940)
    private int year;
    private boolean drivable;
    @Positive
    private Long engineId;
    @Positive
    private long priceInCents;
}
