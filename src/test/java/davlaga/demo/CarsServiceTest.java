package davlaga.demo;

import davlaga.demo.cars.Services.car.CarsService;
import davlaga.demo.cars.Services.engine.EngineService;
import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.model.UserUpdateRequest;
import davlaga.demo.cars.persistence.entities.Car;
import davlaga.demo.cars.persistence.repositories.CarRepository;
import davlaga.demo.cars.user.UserService;
import davlaga.demo.cars.user.persistence.AppUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarsServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private EngineService engineService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;  // Mock Authentication

    @InjectMocks
    private CarsService carsService;

    private AppUser user;
    private Car car;

    @Test
    void buyCar_CarNotFound_ThrowsException() {
        // Arrange
        when(userService.getUser("testuser1")).thenReturn(user);
        when(carRepository.findById(16L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> carsService.buyCar("testuser1", 16L));
        verify(carRepository, never()).save(any());
    }

}