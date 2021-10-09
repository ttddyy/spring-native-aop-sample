package com.example.demonativeaop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.ProxyBits;

@SpringBootApplication
@AotProxyHint(targetClass=com.example.demonativeaop.HiService.class, proxyFeatures = ProxyBits.IS_STATIC)
@AotProxyHint(targetClass=com.example.demonativeaop.GreetingService.class, proxyFeatures = ProxyBits.IS_STATIC)
public class DemoNativeAopApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoNativeAopApplication.class, args);
    }

    @Bean
    public MyAspect myAspect() {
        return new MyAspect();
    }

    @Bean
    public HelloServiceImpl helloService() {
        return new HelloServiceImpl();
    }

    @Bean
    public HiService hiService() {
        return new HiService();
    }

    @Bean
    public CommandLineRunner runner(HelloService helloService, HiService hiService, GreetingService greetingService) {
        return (args) -> {
            System.out.println("HelloService: " + helloService.hello());
            System.out.println("HiService: " + hiService.hi());
            System.out.println("GreetingService: " + greetingService.greet());
        };
    }

}
