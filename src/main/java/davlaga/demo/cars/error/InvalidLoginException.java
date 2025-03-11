package davlaga.demo.cars.error;

public class InvalidLoginException extends RuntimeException{

    public InvalidLoginException(String message){
        super(message);
    }
}
