package co.edu.escuelaing.techcup.tournament.infrastructure.aspect;

import co.edu.escuelaing.techcup.tournament.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.AuditEventRepositoryPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditEventAspectTest {

    private static class TestService {}

    private Signature signatureFor(String methodName) {
        Signature signature = mock(Signature.class);
        doReturn(TestService.class).when(signature).getDeclaringType();
        when(signature.getName()).thenReturn(methodName);
        return signature;
    }

    @Test
    void capture_flujoFeliz_guardaEventoConActionTypeYEntidad() throws Throwable {
        AuditEventRepositoryPort repository = mock(AuditEventRepositoryPort.class);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);

        Signature signature = signatureFor("doSomething");
        when(pjp.getSignature()).thenReturn(signature);
        when(pjp.getArgs()).thenReturn(new Object[]{"t1"});
        when(pjp.proceed()).thenReturn("result");

        AuditEventAspect aspect = new AuditEventAspect(repository);
        Object result = aspect.capture(pjp);

        assertEquals("result", result);
        verify(repository).save(any(AuditEvent.class));
    }

    @Test
    void capture_cuandoProceedLanzaExcepcion_sePropagaYNoSeGuardaEvento() throws Throwable {
        AuditEventRepositoryPort repository = mock(AuditEventRepositoryPort.class);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);
        when(pjp.proceed()).thenThrow(new RuntimeException("falla de negocio"));

        AuditEventAspect aspect = new AuditEventAspect(repository);

        assertThrows(RuntimeException.class, () -> aspect.capture(pjp));
        verify(repository, never()).save(any());
    }

    @Test
    void capture_cuandoFallaElGuardadoDeAuditoria_igualRetornaElResultadoOriginal() throws Throwable {
        AuditEventRepositoryPort repository = mock(AuditEventRepositoryPort.class);
        ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);

        Signature signature = signatureFor("doSomething");
        when(pjp.getSignature()).thenReturn(signature);
        when(pjp.getArgs()).thenReturn(new Object[]{"t1"});
        when(pjp.proceed()).thenReturn("result");
        doThrow(new RuntimeException("mongo caído")).when(repository).save(any());

        AuditEventAspect aspect = new AuditEventAspect(repository);
        Object result = aspect.capture(pjp);

        assertEquals("result", result);
    }
}
