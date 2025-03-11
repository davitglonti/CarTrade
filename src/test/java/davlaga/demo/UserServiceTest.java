package davlaga.demo;

import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.model.UserUpdateRequest;
import davlaga.demo.cars.user.RoleService;
import davlaga.demo.cars.user.UserService;
import davlaga.demo.cars.user.persistence.AppUser;
import davlaga.demo.cars.user.persistence.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @Mock
    private Authentication authentication; //  Authentication mock

    @InjectMocks
    private UserService userService;

    private AppUser user;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1002L);
        user.setUsername("testuser1");
        user.setPassword("encodedPassword");
        user.setBalanceInCents(5000000L);
    }

    @Test
    void updateUser_Success() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newUsername");
        request.setPassword("newPassword");
        request.setBalanceInCents(6000000);

        when(appUserRepository.findById(1002L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(authentication.getName()).thenReturn("testuser1"); // ✅ მომხმარებელი თვითონვე ცვლის მონაცემებს

        // Act
        userService.updateUser(1002L, request, authentication);

        // Assert
        assertEquals("newUsername", user.getUsername());
        assertEquals("newEncodedPassword", user.getPassword());
        assertEquals(6000000, user.getBalanceInCents());
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newUsername");

        when(appUserRepository.findById(1002L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> userService.updateUser(1002L, request, authentication));
        verify(appUserRepository, never()).save(any());
    }

    @Test
    void updateUser_AccessDenied_ThrowsException() {
        // Arrange
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newUsername");

        when(appUserRepository.findById(1002L)).thenReturn(Optional.of(user));
        when(authentication.getName()).thenReturn("anotherUser"); // ❌ სხვა მომხმარებელი ცდილობს ცვლილებებს

        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> userService.updateUser(1002L, request, authentication));
        verify(appUserRepository, never()).save(any());
    }
}

