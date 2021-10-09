package com.example.demonativeaop;

import org.springframework.stereotype.Service;

//@Greeter
@Service
public class GreetingService {

    @Greeter
    public String greet() {
        return "Greeting";
    }

}
