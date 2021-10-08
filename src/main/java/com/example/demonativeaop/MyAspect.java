package com.example.demonativeaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Aspect
//@Component
//@Configuration(proxyBeanMethods = false)
public class MyAspect {

    @Around(value = "execution(* com.example.demonativeaop.HelloService.*(..))")
    public String introduction(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed() + " (intercepted)";
    }

}
