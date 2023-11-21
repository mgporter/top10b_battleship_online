package io.mgporter.battleship_online;

import java.util.Arrays;
import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAspectJAutoProxy
public class LoggingAspect {

  private final Logger logger = Logger.getLogger(LoggingAspect.class.getName());

  @Around("execution(* io.mgporter.battleship_online..*.*(..))")
  public Object loggingAspect(ProceedingJoinPoint joinPoint) throws Throwable {

    String className = joinPoint.getTarget().getClass().getSimpleName();
    String methodName = joinPoint.getSignature().getName();
    Object[] arguments = joinPoint.getArgs();

    // logger.info(className + "." + methodName + " called with arguments: " + Arrays.asList(arguments) + "\n");
    System.out.println(className + "." + methodName + " called with arguments: " + Arrays.asList(arguments) + "\n");

    return joinPoint.proceed();
  }

}
