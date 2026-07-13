package co.edu.escuelaing.techcup.tournament.aspect;

import co.edu.escuelaing.techcup.tournament.service.AuditEvent;
import co.edu.escuelaing.techcup.tournament.service.ports.AuditEventRepositoryPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Captura transversal de eventos de auditoría (TC-40) sobre todas las
 * acciones de application services (service.impl.*), sin modificar ninguno
 * de esos ~23 archivos. Solo registra acciones que terminan exitosamente
 * (si el método intercepted lanza excepción, no se genera evento). El actor
 * es un placeholder fijo ({@link AuditEvent#SYSTEM_ACTOR}) pendiente de
 * integración con el futuro Servicio de Identidad. La entidad afectada se
 * extrae de forma best-effort del primer argumento del método.
 */
@Aspect
@Component
public class AuditEventAspect {

    private static final Logger log = LoggerFactory.getLogger(AuditEventAspect.class);

    private final AuditEventRepositoryPort repository;

    public AuditEventAspect(AuditEventRepositoryPort repository) {
        this.repository = repository;
    }

    @Around("execution(public * co.edu.escuelaing.techcup.tournament.service.impl..*.*(..))")
    public Object capture(ProceedingJoinPoint pjp) throws Throwable {
        Object result = pjp.proceed();

        try {
            String actionType = pjp.getSignature().getDeclaringType().getSimpleName()
                    + "." + pjp.getSignature().getName();
            String affectedEntity = extractAffectedEntity(pjp.getArgs());
            repository.save(AuditEvent.create(AuditEvent.SYSTEM_ACTOR, actionType, affectedEntity));
        } catch (RuntimeException e) {
            log.warn("No se pudo registrar el evento de auditoría para {}", pjp.getSignature(), e);
        }

        return result;
    }

    private String extractAffectedEntity(Object[] args) {
        if (args == null || args.length == 0 || args[0] == null) return null;
        return args[0] instanceof String s ? s : String.valueOf(args[0]);
    }
}
