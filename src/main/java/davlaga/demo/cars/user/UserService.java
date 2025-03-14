package davlaga.demo.cars.user;

import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.model.UserUpdateRequest;
import davlaga.demo.cars.user.persistence.AppUser;
import davlaga.demo.cars.user.persistence.AppUserRepository;
import davlaga.demo.cars.user.model.UserRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class UserService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    public void createUser(UserRequest userRequest){
        AppUser user = new AppUser();
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setRoles(userRequest.getRoleIds().stream().
                map(roleService::getRole).collect(Collectors.toSet()));

        appUserRepository.save(user);
    }

    public AppUser getUser(String username){
        return appUserRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
    }
    public void updateUser(Long id, UserUpdateRequest userUpdateRequest, Authentication authentication) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        String loggedInUsername = authentication.getName(); //Authorized user

        if (!user.getUsername().equals(loggedInUsername) && authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You can only update your own profile!");
        }

        user.setUsername(userUpdateRequest.getUsername());

        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }

        if (userUpdateRequest.getRoleIds() != null && !userUpdateRequest.getRoleIds().isEmpty()) {
            user.setRoles(userUpdateRequest.getRoleIds().stream()
                    .map(roleService::getRole)
                    .collect(Collectors.toSet()));
        }

        //  Only update your own balance
        if (userUpdateRequest.getBalanceInCents() != null && userUpdateRequest.getBalanceInCents() >= 0) {
            user.setBalanceInCents(userUpdateRequest.getBalanceInCents());
        }

        appUserRepository.save(user);
    }
    public void updateBalance(Long id, Long balanceInCents, Authentication authentication) {
        AppUser user = appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));

        String loggedInUsername = authentication.getName(); // Auth User

        //   user or ADMIN
        if (!user.getUsername().equals(loggedInUsername) && authentication.getAuthorities().stream()
                .noneMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            throw new AccessDeniedException("You can only update your own balance!");
        }

        user.setBalanceInCents(balanceInCents);
        appUserRepository.save(user);
    }


}


