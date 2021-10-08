package com.example.demonativeaop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoNativeAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoNativeAopApplication.class, args);
    }

    @Bean
    public HelloService helloService() {
        return new HelloServiceImpl();
    }

    @Bean
    public CommandLineRunner runner(HelloService helloService) {
        return (args) -> {
            String hello = helloService.hello();
            System.out.println("Service: " + hello);
        };
    }

    @Bean
    public MyAspect myAspect() {
        return new MyAspect();
    }

}
