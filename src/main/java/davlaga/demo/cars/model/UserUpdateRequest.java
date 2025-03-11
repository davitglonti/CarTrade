package davlaga.demo.cars.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;
@Data
public class UserUpdateRequest {
    private String username;
    private String password;
    private Set<Long> roleIds;
    private Integer balanceInCents;


}
