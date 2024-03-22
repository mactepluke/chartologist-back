package co.syngleton.chartomancer.user_controller;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class UserRequestLoggingAspect {

    @Before("execution(* co.syngleton.chartomancer.user_controller.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Received request: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning(pointcut = "execution(* co.syngleton.chartomancer.user_controller.*.*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Completed request: {}, result: {}", joinPoint.getSignature().toShortString(), result);
    }
}
