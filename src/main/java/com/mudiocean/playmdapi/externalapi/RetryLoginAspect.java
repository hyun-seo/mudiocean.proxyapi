package com.mudiocean.playmdapi.externalapi;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RetryLoginAspect {
    @Pointcut("@annotation(com.mudiocean.playmdapi.externalapi.RetryLogin)")
    private void enableRetryLogin() {}

    @Around("enableRetryLogin()")
    public Object doRetry(ProceedingJoinPoint joinPoint) throws Throwable {

        int maxRetry = 3;
        Exception exceptionHolder = null;

        for (int retryCount = 1; retryCount <= maxRetry; retryCount++) {
            try {
                return joinPoint.proceed();
            } catch (Exception e) {
                joinPoint.getClass().getMethod("login").invoke(joinPoint);
                exceptionHolder = e;
            }
        }
        throw exceptionHolder;
    }
}