# About

Sample AOP code with Spring Native.

----

# `@Aspect` class

`MyAspect` class is annotated with `@Aspect`.

## Declared as `@Bean`

When `MyAspect` is declared as a `@Bean`:
```java
    @Bean
    public MyAspect myAspect() {
        return new MyAspect();
    }
```

The `reflect-config.json` requires this entry.
```json
  {
    "name": "com.example.demonativeaop.MyAspect",
    "allDeclaredMethods": true
  }
```

This is because methods in the `@Aspect` class define pointcuts, but usually they are not referenced by application.
Those methods need to be retained to create AOP proxies.

### Improvement idea

Automatically add hint when a bean with `@Aspect` is found.

## Annotated as `@Component`

When `MyAspect` class is annotated with `@Componenet`, component scan finds the bean.
In this case, no manual configuration is required. 

```java
@Aspect
//@Component
public class MyAspect {
    ...
}
```

The generated `reflect-config.json` has this entry:
```json lines
  {
    "name": "com.example.demonativeaop.MyAspect",
    "allDeclaredFields": true,
    "allDeclaredConstructors": true,
    "allDeclaredMethods": true,
    "allPublicMethods": true,
    "allDeclaredClasses": true,
    "methods": [
      {
        "name": "<init>",
        "parameterTypes": []
      }
    ]
  }
```

# Reference in AOP expression

For a pointcut expression `execution(* com.example.demonativeaop.HiService.*(..))`, the `reflect-config.json` requires this entry:

```json
  {
    "name": "com.example.demonativeaop.HiService",
    "allDeclaredMethods": true
  }
```

Otherwise, this exception is thrown at runtime:
```
...
Caused by: java.lang.IllegalArgumentException: warning no match for this type name: com.example.demonativeaop.HiService [Xlint:invalidAbsoluteTypeName]
	at org.aspectj.weaver.tools.PointcutParser.parsePointcutExpression(PointcutParser.java:319) ~[na:na]
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.buildPointcutExpression(AspectJExpressionPointcut.java:227) ~[na:na]
```

For annotation based pointcut, `@annotation(com.example.demonativeaop.Greeter)`, it requires the annotation to be in `reflect-config.json` as well.
```json
  {
    "name": "com.example.demonativeaop.Greeter",
    "allDeclaredMethods": true
  }
```

Without the entry, this error is thrown:
```
...
Caused by: java.lang.IllegalArgumentException: error Type referred to is not an annotation type: com$example$demonativeaop$Greeter
	at org.aspectj.weaver.tools.PointcutParser.parsePointcutExpression(PointcutParser.java:319) ~[na:na]
	at org.springframework.aop.aspectj.AspectJExpressionPointcut.buildPointcutExpression(AspectJExpressionPointcut.java:227) ~[na:na]
```

## Improvement idea

It would be nice if AOT parses the AOP expression and figures out any classes that has reference to, then auto generate hints for them. 


# AOP Proxies

In spring-native, there are two phases for AOP proxies.

The first phase is at AOT.  
For class based proxies, `ConfigurationContributor` and `ProxyGenerator` generate proxy classes with ByteBuddy(e.g. `com.example.demonativeaop.HiService$$SpringProxy$7f5de2cd`) and write class files under `target/generated-sources/spring-aot/src/main/resources/../*.class`.  
Also, it adds the generated proxies to the `reflect-config.json` in order for runtime to read them.

The next phase is at runtime.  
The `Target_DefaultAopProxyFactory` substitutes the AOP proxy creation.
For class based proxies, it uses `BuildTimeAopProxy` which loads the ByteBuddy generated proxies and instantiate them. 

To bridge these two phases, the `ProxyConfiguration#getProxyClassName()` method guarantees the same name is generated based on the advised interfaces. 

## Class based proxy:

The `HiService` is a class based proxy service.
In order to generate a proxy for this class at build time, this hint is required.

```
@AotProxyHint(targetClass=com.example.demonativeaop.HiService.class, proxyFeatures = ProxyBits.IS_STATIC)
```

The hint is read by `ConfigurationContributor#generateBuildTimeClassProxies` and generates ByteBuddy proxy classes.(e.g. `com.example.demonativeaop.HiService$$SpringProxy$7f5de2cd`)

### Improvement Idea
Currently, user needs to add `@AotProxyHint`. 
It would be nice if this is not required; so that, build time automatically detects class based proxies for AOP and generates/persists proxies.

## Interface based proxy

The `HelloService` is interfaced based proxy service. (with `HelloServiceImpl`)

This uses `JdkDynamicAopProxy` to generate AOP proxy at runtime.

Since this is a regular dynamic proxy at runtime, `proxy-config.json` requires this entry.

```json
  [
    "com.example.demonativeaop.HelloService",
    "org.springframework.aop.SpringProxy",
    "org.springframework.aop.framework.Advised",
    "org.springframework.core.DecoratingProxy"
  ]
```

### Improvement idea

Since this proxy entry is infrastructure logic to generate dynamic proxy, it would be nice AOT automatically adds this hint at build time.
