package davlaga.demo.cars.user;

import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.user.persistence.Role;
import davlaga.demo.cars.user.persistence.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRole(Long id){
        return roleRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Role with id: "+ id + " does not exists"));
    }
}
