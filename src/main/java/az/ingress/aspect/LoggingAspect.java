package az.ingress.aspect;

import az.ingress.logger.ApplicationLogger;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final ApplicationLogger logger = ApplicationLogger.getLogger(LoggingAspect.class);

    @SneakyThrows
    @Around("execution(* az.ingress.service..*(..))")
    public Object logServiceMethods(ProceedingJoinPoint jp) {

        var methodName = jp.getSignature().getName();
        Object[] args = jp.getArgs();
        var params = Arrays.toString(args);

        logger.info("ActionLog.{}.start - {}", methodName, params);

        try {
            var result = jp.proceed();
            logger.info("ActionLog.{}.end - {}", methodName, params);
            return result;
        } catch (Exception ex) {
            logger.error("ActionLog.{}.error - {} - {}", methodName, params, ex.getMessage());
            throw ex;
        }
    }
}
