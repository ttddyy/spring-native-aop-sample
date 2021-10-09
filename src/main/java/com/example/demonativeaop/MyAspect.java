package com.example.demonativeaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
//@Component
public class MyAspect {

    @Around(value = "execution(* com.example.demonativeaop.HelloService.*(..))")
    public String helloIntroduction(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed() + " (hello intercepted)";
    }

    @Around(value = "execution(* com.example.demonativeaop.HiService.*(..))")
    public String hiIntroduction(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed() + " (hi intercepted)";
    }

    @Around(value = "@annotation(com.example.demonativeaop.Greeter)")
    //    @Around(value = " within(com.example.demonativeaop..*) && @target(com.example.demonativeaop.Greeter)")
    public String greeterIntroduction(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed() + " (greeter intercepted)";
    }

}
