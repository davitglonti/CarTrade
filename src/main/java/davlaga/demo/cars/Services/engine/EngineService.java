package davlaga.demo.cars.Services.engine;

import davlaga.demo.cars.model.EngineDTO;
import davlaga.demo.cars.model.EngineRequest;
import davlaga.demo.cars.persistence.entities.Engine;
import davlaga.demo.cars.persistence.repositories.EngineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EngineService {
    private final EngineRepository engineRepository;

    public Page<EngineDTO> getEngines(int page, int pageSize, double capacity) {
        return engineRepository.findEngines(capacity, PageRequest.of(page,pageSize));
    }

    public void createEngine(EngineRequest request) {
        Engine engine = new Engine();
        engine.setCapacity(request.getCapacity());
        engine.setHorsePower(request.getHorsePower());

        engineRepository.save(engine);
    }

    public EngineDTO updateEngine(Long id, EngineRequest request) {
        Engine engine = engineRepository.findById(id).get();
        engine.setHorsePower(request.getHorsePower());
        engine.setCapacity(request.getCapacity());
        engineRepository.save(engine);

        return mapEngine(engine);
    }



    public void deleteEngine(Long id) {
        engineRepository.deleteById((id));
    }

    private EngineDTO mapEngine(Engine engine) {
        return new EngineDTO(
                engine.getId(),
                engine.getHorsePower(),
                engine.getCapacity()
        );
    }

    public Engine findEngine(Long id) {
        return engineRepository.findById(id).get();
    }
}
