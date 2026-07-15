package co.edu.escuelaing.techcup.tournament.application.usecase;

import co.edu.escuelaing.techcup.tournament.domain.exception.InvalidSanctionDataException;
import co.edu.escuelaing.techcup.tournament.domain.model.PlayerSanction;
import co.edu.escuelaing.techcup.tournament.domain.model.SanctionType;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ApplySanctionUseCase.ApplySanctionCommand;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.PlayerSanctionRepositoryPort;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.out.SanctionNotificationPort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApplySanctionServiceTest {

    @Test
    void apply_redCard_guardaYNotifica() {
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        SanctionNotificationPort notificationPort = mock(SanctionNotificationPort.class);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        ApplySanctionService service = new ApplySanctionService(repository, notificationPort);

        PlayerSanction result = service.apply(new ApplySanctionCommand("p1", SanctionType.RED_CARD, null));

        assertEquals("p1", result.getPlayerId());
        assertEquals(1, result.getMatchesRemaining());
        verify(repository).save(any());
        verify(notificationPort).notifyPlayerSanctioned(eq("p1"), eq(SanctionType.RED_CARD), eq(1));
    }

    @Test
    void apply_conductSinMatchesSuspended_lanzaExceptionSinGuardarNiNotificar() {
        PlayerSanctionRepositoryPort repository = mock(PlayerSanctionRepositoryPort.class);
        SanctionNotificationPort notificationPort = mock(SanctionNotificationPort.class);

        ApplySanctionService service = new ApplySanctionService(repository, notificationPort);

        assertThrows(InvalidSanctionDataException.class,
                () -> service.apply(new ApplySanctionCommand("p1", SanctionType.CONDUCT, null)));

        verify(repository, never()).save(any());
        verify(notificationPort, never()).notifyPlayerSanctioned(any(), any(), anyInt());
    }
}
