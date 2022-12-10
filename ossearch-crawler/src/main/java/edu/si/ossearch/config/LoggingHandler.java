package edu.si.ossearch.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author jbirkhimer
 */
@Aspect
@Component
@Profile("test")
public class LoggingHandler {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut("this(org.springframework.data.repository.Repository)")
    public void repository() { }

    @Pointcut("execution(* *.*(..))")
    protected void allMethod() { }

    //before -> Any resource annotated with @Repository annotation
    //and all method and function
    @Before("repository() && allMethod()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering in Method :  " +
                joinPoint + " at " + System.currentTimeMillis());
    }

    //After -> All method within resource annotated with @Repository annotation
    @AfterReturning(pointcut = "repository() && allMethod()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Method Returned at : " + System.currentTimeMillis());
    }
}
