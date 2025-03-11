package davlaga.demo;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/greet")
@Profile("local")

public class GreetingController {

    private final GreetingService greetingService;

    public  GreetingController(GreetingService greetingService){
        this.greetingService= greetingService;
    }
    @GetMapping
    String greet(@RequestParam String name) {
        return  greetingService.prepareGreeting(name);
    }

    @GetMapping("/{name}")
    String greetToo(@PathVariable String name) {
        return  greetingService.prepareGreeting(name);
    }
}
