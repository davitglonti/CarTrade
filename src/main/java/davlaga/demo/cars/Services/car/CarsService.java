package davlaga.demo.cars.Services.car;

import davlaga.demo.cars.Services.engine.EngineService;
import davlaga.demo.cars.model.UserUpdateRequest;
import davlaga.demo.cars.persistence.entities.TaxAccount;
import davlaga.demo.cars.persistence.repositories.TaxAccountRepository;
import davlaga.demo.cars.user.UserService;
import davlaga.demo.cars.error.NotFoundException;
import davlaga.demo.cars.model.CarDTO;
import davlaga.demo.cars.model.CarRequest;
import davlaga.demo.cars.model.EngineDTO;
import davlaga.demo.cars.persistence.entities.Car;
import davlaga.demo.cars.persistence.repositories.CarRepository;
import davlaga.demo.cars.user.persistence.AdminAccount;
import davlaga.demo.cars.user.persistence.AdminAccountRepository;
import davlaga.demo.cars.user.persistence.AppUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import davlaga.demo.cars.S3Service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarsService {
    private final CarRepository carRepository;
    private final EngineService engineService;
    private final UserService userService;
    private final AdminAccountRepository adminAccountRepository;
    private final TaxAccountRepository taxAccountRepository;
    public Page<CarDTO> getCars(int page, int pageSize) {
        return carRepository.findAll(PageRequest.of(page, pageSize))
                .map(this::mapCar);
    }

    public void addCar(CarRequest request) {
        Car car = new Car();
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setDriveable(request.isDrivable());
        car.setEngine(engineService.findEngine(request.getEngineId()));
        car.setPriceInCents(request.getPriceInCents());
        carRepository.save(car);
    }


    public void updateCar(Long id, CarRequest request) {
        Car car = carRepository.findById(id).orElseThrow(() -> buildNotFoundException(id));
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setDriveable(request.isDrivable());
        car.setPriceInCents(request.getPriceInCents());
        if (car.getEngine().getId() != request.getEngineId()) {
            car.setEngine(engineService.findEngine(request.getEngineId()));
        }
        carRepository.save(car);
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }

    public CarDTO findCar(int id) {
        return carRepository.findById((long) id)
                .map(this::mapCar)
                .orElseThrow(() -> new NotFoundException("Car not found with id: " + id));
    }
    @Transactional
    public void buyCar(String username, Long carId) {
        AppUser user = userService.getUser(username);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with id " + carId + " not found"));

        if (!car.isDriveable()) {
            throw new IllegalStateException("The car is not available for purchase.");
        }

        if (user.getBalanceInCents() < car.getPriceInCents()) {
            throw new IllegalStateException("Insufficient balance to buy car");
        }

        if (car.getReservedBy() != null && !car.getReservedBy().getUsername().equals(username)) {
            throw new IllegalStateException("Car is reserved by another user");
        }
        if (car.getReservedUntil() != null && car.getReservedUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Car is reserved until " + car.getReservedUntil());
        }

        user.setBalanceInCents(user.getBalanceInCents() - car.getPriceInCents());
        user.getOwnedCars().add(car);
        car.getOwners().add(user);
        car.setDriveable(false);

        carRepository.save(car);
    }
    @Transactional
    public void sellCar(String username, Long carId) {
        AppUser user = userService.getUser(username);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with id " + carId + " not found"));

        if (!user.getOwnedCars().contains(car)) {
            throw new IllegalStateException("User does not own this car");
        }

        long salePriceInCents= car.getPriceInCents();
        long sellerShare = (long) (salePriceInCents * 0.80);
        long remainingAmount = salePriceInCents - sellerShare;
        long taxAmount= (long) (remainingAmount*0.20);
        long adminAmount = remainingAmount - taxAmount;

        user.setBalanceInCents(user.getBalanceInCents() + sellerShare);
        user.getOwnedCars().remove(car);
        car.getOwners().remove(user);
        car.setDriveable(true);

        if (car.getReservedBy() != null && !car.getReservedBy().getUsername().equals(username)) {
            throw new IllegalStateException("Car is reserved by another user");
        }

        // Admin account update
        AdminAccount adminAccount = adminAccountRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException("Admin account not found"));
        adminAccount.setBalanceInCents(adminAccount.getBalanceInCents()+adminAmount);

        TaxAccount taxAccount = taxAccountRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException("Tax account not found"));
        taxAccount.setBalanceInCents(taxAccount.getBalanceInCents() + taxAmount);

        carRepository.save(car);
        adminAccountRepository.save(adminAccount);
        taxAccountRepository.save(taxAccount);

    }

    public Page<CarDTO> getOwnedCars(String username, int page, int pageSize) {
        return carRepository.findByOwnerUsername(username, PageRequest.of(page, pageSize))
                .map(this::mapCar);
    }

    @Transactional
    public void reserveCar(String username, Long carId) {
        AppUser user = userService.getUser(username);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Car with id " + carId + " not found"));

        if (!car.isDrivable()) {
            throw new IllegalStateException("Car isnt available for reservation");
        }

        if (car.getReservedUntil() != null && car.getReservedUntil().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Car is already reserved until " + car.getReservedUntil());
        }

        car.setDriveable(false);
        car.setReservedBy(user);
        car.setReservedUntil(LocalDateTime.now().plusHours(1)); // დაჯავშნა 24 საათით

        carRepository.save(car);

    }

    public CarDTO mapCar(Car car) {
        return new CarDTO(
                car.getId(),
                car.getModel(),
                car.getYear(),
                car.isDrivable(),
                new EngineDTO(car.getEngine().getId(), car.getEngine().getHorsePower(), car.getEngine().getCapacity()),
                car.getPriceInCents(),
                car.getOwners().stream().map(AppUser::getUsername).collect(Collectors.toSet()),
                car.getPhotoUrl()
        );
    }

    private NotFoundException buildNotFoundException(Long id) {
        return new NotFoundException("Car with id " + id + " not found");
    }


    @Autowired
    private S3Service s3Service;

    public String uploadCarPhoto(Long carId, MultipartFile photo) throws IOException {
        String photoUrl = s3Service.uploadFile(carId, photo);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found with id: " + carId));
        car.setPhotoUrl(photoUrl);
        carRepository.save(car);
        return photoUrl;
    }
}

