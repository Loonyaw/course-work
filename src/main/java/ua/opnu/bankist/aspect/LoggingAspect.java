package ua.opnu.bankist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.opnu.bankist.annotations.LogController;
import ua.opnu.bankist.annotations.LogService;
import ua.opnu.bankist.annotations.LogRepository;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@within(logController) || @annotation(logController)")
    public Object logController(ProceedingJoinPoint joinPoint, LogController logController) throws Throwable {
        return log(joinPoint, "Controller");
    }

    @Around("@within(logService) || @annotation(logService)")
    public Object logService(ProceedingJoinPoint joinPoint, LogService logService) throws Throwable {
        return log(joinPoint, "Service");
    }

    @Around("@within(logRepository) || @annotation(logRepository)")
    public Object logRepository(ProceedingJoinPoint joinPoint, LogRepository logRepository) throws Throwable {
        return log(joinPoint, "Repository");
    }

    private Object log(ProceedingJoinPoint joinPoint, String layer) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        logger.info("{} - Before invocation: {}", layer, methodName);
        Object result;
        try {
            result = joinPoint.proceed();
            logger.info("{} - After invocation: {}", layer, methodName);
        } catch (Throwable throwable) {
            logger.error("{} - Exception in: {}", layer, methodName, throwable);
            throw throwable;
        }
        return result;
    }
}
