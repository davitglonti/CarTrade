
package davlaga.demo.cars.user;

import davlaga.demo.cars.model.UserUpdateRequest;
import davlaga.demo.cars.user.model.UserRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static davlaga.demo.cars.security.AuthorizationConstants.ADMIN;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize(ADMIN)
public class UserController {

    private final UserService userService;

    @PostMapping
    public void createUser(@RequestBody @Valid UserRequest userRequest){
        userService.createUser(userRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public void updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request, Authentication authentication) {
        userService.updateUser(id, request, authentication);
    }

    @PutMapping("/{id}/balance")
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateBalance(
            @PathVariable Long id,
            @RequestBody @Valid BalanceUpdateRequest request,
            Authentication authentication) {
        // დასაზუსტებელი: დავრწმუნდეთ რომ request.getBalanceInCents() ზუსტია
        userService.updateBalance(id, request.getBalanceInCents(), authentication);
        return ResponseEntity.ok("Balance updated successfully");
    }

    @Data
    public static class BalanceUpdateRequest {
        @NotNull(message = "Balance cannot be null")
        @Min(value = 0, message = "Balance cannot be negative")
        private Long balanceInCents;
    }

}


