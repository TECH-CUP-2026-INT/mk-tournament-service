package co.edu.escuelaing.techcup.tournament.infrastructure.in.messaging;

import co.edu.escuelaing.techcup.tournament.domain.model.MatchPhase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase;
import co.edu.escuelaing.techcup.tournament.domain.service.ports.in.ProcessMatchResultUseCase.ProcessMatchResultCommand;
import co.edu.escuelaing.techcup.tournament.infrastructure.in.messaging.dto.MatchFinishedEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MatchFinishedListenerTest {

    @Test
    void onMatchFinished_delegaAlUseCaseConElComandoCorrecto() {
        ProcessMatchResultUseCase processMatchResult = mock(ProcessMatchResultUseCase.class);
        MatchFinishedListener listener = new MatchFinishedListener(processMatchResult);

        UUID matchId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID ganadorId = UUID.randomUUID();
        MatchFinishedEvent event = new MatchFinishedEvent(
                matchId, tournamentId, MatchPhase.ELIMINATORIA, 2, 1, ganadorId, UUID.randomUUID(), null, Instant.now());

        listener.onMatchFinished(event);

        verify(processMatchResult).process(new ProcessMatchResultCommand(
                matchId, tournamentId, MatchPhase.ELIMINATORIA, 2, 1, ganadorId, null));
    }

    @Test
    void onMatchFinished_conAusenteId_loPropagaAlComando() {
        ProcessMatchResultUseCase processMatchResult = mock(ProcessMatchResultUseCase.class);
        MatchFinishedListener listener = new MatchFinishedListener(processMatchResult);

        UUID matchId = UUID.randomUUID();
        UUID tournamentId = UUID.randomUUID();
        UUID presentTeamId = UUID.randomUUID();
        UUID ausenteId = UUID.randomUUID();
        MatchFinishedEvent event = new MatchFinishedEvent(
                matchId, tournamentId, MatchPhase.GRUPOS, 0, 0, presentTeamId, null, ausenteId, Instant.now());

        listener.onMatchFinished(event);

        verify(processMatchResult).process(new ProcessMatchResultCommand(
                matchId, tournamentId, MatchPhase.GRUPOS, 0, 0, presentTeamId, ausenteId));
    }
}
