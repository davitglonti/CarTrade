package davlaga.demo.cars.Controllers.car;
import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.user.UserService;
import davlaga.demo.cars.user.persistence.AdminAccount;
import davlaga.demo.cars.user.persistence.AdminAccountRepository;
import davlaga.demo.cars.user.persistence.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import davlaga.demo.cars.Services.car.CarsService;
import davlaga.demo.cars.model.CarDTO;
import davlaga.demo.cars.model.CarRequest;
import davlaga.demo.cars.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static davlaga.demo.cars.security.AuthorizationConstants.ADMIN;
import static davlaga.demo.cars.security.AuthorizationConstants.USER_OR_ADMIN;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarsController {

    private final CarsService carsService;
    private final AdminAccountRepository adminAccountRepository;
    @PostMapping
    @PreAuthorize(ADMIN)
    void addCar(@RequestBody @Valid CarRequest request) {
        carsService.addCar(request);
    }

    @PutMapping("{id}")
    @PreAuthorize(ADMIN)
    void updateCar(@PathVariable Long id, @RequestBody @Valid CarRequest request){
        carsService.updateCar(id, request);
    }
    @DeleteMapping("{id}")
    void deleteCar(@PathVariable Long id) {
        carsService.deleteCar(id);
    }
    @GetMapping("{id}")
    CarDTO getCar(@PathVariable int id) {
        return carsService.findCar(id);
    }

    @GetMapping
    @PreAuthorize(USER_OR_ADMIN)
    Page<CarDTO> getCars(@RequestParam int page, @RequestParam int pageSize) {
        return carsService.getCars(page,pageSize);
    }

    @PostMapping("/{id}/buy")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    void buyCar(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        carsService.buyCar(username, id);
    }
    @PostMapping("/{id}/sell")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    void sellCar(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        carsService.sellCar(username, id);
    }

    @GetMapping("/owned")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    Page<CarDTO> getOwnedCars(@RequestParam int page, @RequestParam int pageSize, Authentication authentication) {
        String username = authentication.getName();
        return carsService.getOwnedCars(username, page, pageSize);
    }


    @PostMapping("/{id}/photo")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String uploadCarPhoto(@PathVariable Long id, @RequestPart("photo") MultipartFile photo) {
        try {
            return carsService.uploadCarPhoto(id, photo);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload photo", e);
        }
    }

    @GetMapping("/admin/balance")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Long> getAdminBalance() {
        AdminAccount adminAccount = adminAccountRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));
        return ResponseEntity.ok(adminAccount.getBalanceInCents());
    }

    @PostMapping("/{carId}/reserve")
    //@PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> reserveCar(
            @PathVariable Long carId,
            @AuthenticationPrincipal UserDetails userDetails) {
        carsService.reserveCar(userDetails.getUsername(), carId);
        return ResponseEntity.ok("Car reserved successfully");
    }

}
