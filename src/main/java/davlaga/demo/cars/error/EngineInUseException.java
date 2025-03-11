package davlaga.demo.cars.error;

public class EngineInUseException extends RuntimeException {
    public EngineInUseException(String message) {
        super(message);
    }
}
