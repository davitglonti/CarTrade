package davlaga.demo;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    public String prepareGreeting(String name) {
        return "hello"+name;
    }


}
